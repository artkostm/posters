package com.github.artkostm.models
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.{
  FromEntityUnmarshaller,
  FromRequestUnmarshaller,
  FromStringUnmarshaller,
  Unmarshal,
  Unmarshaller
}
import akka.http.scaladsl.marshalling.{Marshal, Marshaller, Marshalling, ToEntityMarshaller, ToResponseMarshaller}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{
  Directive,
  Directive0,
  Directive1,
  ExceptionHandler,
  MalformedHeaderRejection,
  MissingFormFieldRejection,
  Rejection,
  Route
}
import akka.http.scaladsl.util.FastFuture
import akka.stream.{IOResult, Materializer}
import akka.stream.scaladsl.{FileIO, Keep, Sink, Source}
import akka.util.ByteString
import io.circe.Decoder
import cats.{Functor, Id}
import cats.data.EitherT
import cats.implicits._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import scala.language.implicitConversions
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicReference
import scala.util.{Failure, Success}
import cats.implicits._
import cats.data.EitherT
import scala.concurrent.Future
import com.github.artkostm.models.Implicits._
object AkkaHttpImplicits {
  private[this] def pathEscape(s: String): String                   = Uri.Path.Segment.apply(s, Uri.Path.Empty).toString
  implicit def addShowablePath[T](implicit ev: Show[T]): AddPath[T] = AddPath.build[T](v => pathEscape(ev.show(v)))
  private[this] def argEscape(k: String, v: String): String         = Uri.Query.apply((k, v)).toString
  implicit def addShowableArg[T](implicit ev: Show[T]): AddArg[T] =
    AddArg.build[T](key => v => argEscape(key, ev.show(v)))
  type HttpClient   = HttpRequest => Future[HttpResponse]
  type TraceBuilder = String => HttpClient => HttpClient
  class TextPlain(val value: String)
  object TextPlain {
    def apply(value: String): TextPlain = new TextPlain(value)
    implicit final def textTEM: ToEntityMarshaller[TextPlain] =
      Marshaller.withFixedContentType(ContentTypes.`text/plain(UTF-8)`) { text =>
        HttpEntity(ContentTypes.`text/plain(UTF-8)`, text.value)
      }
  }
  sealed trait IgnoredEntity
  object IgnoredEntity { val empty: IgnoredEntity = new IgnoredEntity {} }
  implicit final def jsonMarshaller(implicit printer: Printer = Printer.noSpaces): ToEntityMarshaller[io.circe.Json] =
    Marshaller.withFixedContentType(MediaTypes.`application/json`) { json =>
      HttpEntity(MediaTypes.`application/json`, printer.pretty(json))
    }
  implicit final def jsonEntityMarshaller[A](implicit J: io.circe.Encoder[A],
                                             printer: Printer = Printer.noSpaces): ToEntityMarshaller[A] =
    jsonMarshaller(printer).compose(J.apply)
  final val stringyJsonEntityUnmarshaller: FromEntityUnmarshaller[io.circe.Json] = Unmarshaller.byteStringUnmarshaller
    .forContentTypes(MediaTypes.`text/plain`)
    .map({
      case ByteString.empty =>
        throw Unmarshaller.NoContentException
      case data =>
        Json.fromString(data.decodeString("utf-8"))
    })
  implicit final val structuredJsonEntityUnmarshaller: FromEntityUnmarshaller[io.circe.Json] =
    Unmarshaller.byteStringUnmarshaller.forContentTypes(MediaTypes.`application/json`).flatMapWithInput {
      (httpEntity, byteString) =>
        if (byteString.isEmpty) {
          FastFuture.failed(Unmarshaller.NoContentException)
        } else {
          val parseResult = Unmarshaller.bestUnmarshallingCharsetFor(httpEntity) match {
            case HttpCharsets.`UTF-8` =>
              jawn.parse(byteString.utf8String)
            case otherCharset =>
              jawn.parse(byteString.decodeString(otherCharset.nioCharset.name))
          }
          parseResult.fold(FastFuture.failed, FastFuture.successful)
        }
    }
  implicit def jsonEntityUnmarshaller[A](implicit J: io.circe.Decoder[A]): FromEntityUnmarshaller[A] =
    Unmarshaller
      .firstOf(structuredJsonEntityUnmarshaller, stringyJsonEntityUnmarshaller)
      .flatMap(_ => _ => json => J.decodeJson(json).fold(FastFuture.failed, FastFuture.successful))
  final val jsonStringUnmarshaller: FromStringUnmarshaller[io.circe.Json] = Unmarshaller.strict({
    case "" =>
      throw Unmarshaller.NoContentException
    case data =>
      jawn.parse(data).getOrElse(Json.fromString(data))
  })
  implicit def jsonDecoderUnmarshaller[A](implicit J: io.circe.Decoder[A]): FromStringUnmarshaller[A] =
    jsonStringUnmarshaller.flatMap(_ => _ => json => J.decodeJson(json).fold(FastFuture.failed, FastFuture.successful))
  implicit val ignoredUnmarshaller: FromEntityUnmarshaller[IgnoredEntity] =
    Unmarshaller.strict(_ => IgnoredEntity.empty)
  implicit def MFDBPviaFSU[T](
      implicit ev: Unmarshaller[BodyPartEntity, T]): Unmarshaller[Multipart.FormData.BodyPart, T] =
    Unmarshaller.withMaterializer { implicit executionContext => implicit mat =>
      { entity =>
        ev.apply(entity.entity)
      }
    }
  implicit def BPEviaFSU[T](implicit ev: Unmarshaller[String, T]): Unmarshaller[BodyPartEntity, T] =
    Unmarshaller.withMaterializer { implicit executionContext => implicit mat =>
      { entity =>
        entity.dataBytes
          .runWith(Sink.fold(ByteString.empty)((accum, bs) => accum.concat(bs)))
          .map(_.decodeString(java.nio.charset.StandardCharsets.UTF_8))
          .flatMap(ev.apply(_))
      }
    }
  def AccumulatingUnmarshaller[T, U, V](accumulator: AtomicReference[List[V]], ev: Unmarshaller[T, U])(acc: U => V)(
      implicit mat: Materializer): Unmarshaller[T, U] =
    ev.map { value =>
      accumulator.updateAndGet(acc(value) :: _)
      value
    }
  def SafeUnmarshaller[T, U](ev: Unmarshaller[T, U])(
      implicit mat: Materializer): Unmarshaller[T, Either[Throwable, U]] = Unmarshaller {
    implicit executionContext => entity =>
      ev.apply(entity)
        .map[Either[Throwable, U]](Right(_))
        .recover({
          case t =>
            Left(t)
        })
  }
  def StaticUnmarshaller[T](value: T)(implicit mat: Materializer): Unmarshaller[Multipart.FormData.BodyPart, T] =
    Unmarshaller { _ => part =>
      {
        part.entity.discardBytes()
        Future.successful[T](value)
      }
    }
  implicit def UnitUnmarshaller(implicit mat: Materializer): Unmarshaller[Multipart.FormData.BodyPart, Unit] =
    StaticUnmarshaller(())
}
