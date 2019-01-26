package com.github.artkostm.server.Intent
import cats.data.EitherT
import cats.implicits._
import cats.effect.IO
import cats.effect.Effect
import org.http4s.{Status => _, _}
import org.http4s.circe._
import org.http4s.client.{Client => Http4sClient}
import org.http4s.client.blaze._
import org.http4s.client.UnexpectedStatus
import org.http4s.dsl.io.Path
import org.http4s.multipart._
import org.http4s.headers._
import org.http4s.EntityEncoder._
import org.http4s.EntityDecoder._
import fs2.Stream
import io.circe.Json
import scala.language.higherKinds
import scala.language.implicitConversions
import org.http4s.dsl.Http4sDsl
import fs2.text._
import _root_.com.github.artkostm.server.Implicits._
import _root_.com.github.artkostm.server.Http4sImplicits._
import _root_.com.github.artkostm.server.definitions._
trait IntentHandler[F[_]] {
  def upsertIntent(respond: UpsertIntentResponse.type)(body: Option[IntentBody] = None): F[UpsertIntentResponse]
  def getIntentByName(respond: GetIntentByNameResponse.type)(date: String, name: String): F[GetIntentByNameResponse]
  def deleteIntent(respond: DeleteIntentResponse.type)(body: Option[IntentBody] = None): F[DeleteIntentResponse]
}
class IntentResource[F[_]]()(implicit E: Effect[F]) extends Http4sDsl[F] {
  val upsertIntentDecoder: EntityDecoder[F, Option[IntentBody]] = decodeBy(MediaType.text.plain) { msg =>
    msg.contentLength.filter(_ > 0).fold[DecodeResult[F, Option[IntentBody]]](DecodeResult.success(None)) { _ =>
      DecodeResult.success(decodeString(msg)).flatMap { str =>
        Json
          .fromString(str)
          .as[Option[IntentBody]]
          .fold(failure =>
                  DecodeResult.failure(InvalidMessageBodyFailure(s"Could not decode response: $str", Some(failure))),
                DecodeResult.success(_))
      }
    }
  }
  val upsertIntentOkEncoder = jsonEncoderOf[F, Assign]
  object GetIntentByNameDateMatcher extends QueryParamDecoderMatcher[String]("date")
  object GetIntentByNameNameMatcher extends QueryParamDecoderMatcher[String]("name")
  val getIntentByNameOkEncoder = jsonEncoderOf[F, Assign]
  val deleteIntentDecoder: EntityDecoder[F, Option[IntentBody]] = decodeBy(MediaType.text.plain) { msg =>
    msg.contentLength.filter(_ > 0).fold[DecodeResult[F, Option[IntentBody]]](DecodeResult.success(None)) { _ =>
      DecodeResult.success(decodeString(msg)).flatMap { str =>
        Json
          .fromString(str)
          .as[Option[IntentBody]]
          .fold(failure =>
                  DecodeResult.failure(InvalidMessageBodyFailure(s"Could not decode response: $str", Some(failure))),
                DecodeResult.success(_))
      }
    }
  }
  val deleteIntentOkEncoder = jsonEncoderOf[F, Assign]
  def routes(handler: IntentHandler[F]): HttpRoutes[F] = HttpRoutes.of {
    {
      case req @ POST -> Root / "intents" / "" =>
        req.decodeWith(upsertIntentDecoder, strict = false) { body =>
          handler.upsertIntent(UpsertIntentResponse)(body) flatMap {
            case UpsertIntentResponse.Ok(value) =>
              Ok(value)(E, upsertIntentOkEncoder)
          }
        }
      case req @ GET -> Root / "intents" / "" :? GetIntentByNameDateMatcher(date) +& GetIntentByNameNameMatcher(name) =>
        handler.getIntentByName(GetIntentByNameResponse)(date, name) flatMap {
          case GetIntentByNameResponse.Ok(value) =>
            Ok(value)(E, getIntentByNameOkEncoder)
          case GetIntentByNameResponse.NotFound =>
            NotFound()
        }
      case req @ DELETE -> Root / "intents" / "" =>
        req.decodeWith(deleteIntentDecoder, strict = false) { body =>
          handler.deleteIntent(DeleteIntentResponse)(body) flatMap {
            case DeleteIntentResponse.Ok(value) =>
              Ok(value)(E, deleteIntentOkEncoder)
          }
        }
    }
  }
}
sealed abstract class UpsertIntentResponse {
  def fold[A](handleOk: Assign => A): A = this match {
    case x: UpsertIntentResponse.Ok =>
      handleOk(x.value)
  }
}
object UpsertIntentResponse { case class Ok(value: Assign) extends UpsertIntentResponse }
sealed abstract class GetIntentByNameResponse {
  def fold[A](handleOk: Assign => A, handleNotFound: => A): A = this match {
    case x: GetIntentByNameResponse.Ok =>
      handleOk(x.value)
    case GetIntentByNameResponse.NotFound =>
      handleNotFound
  }
}
object GetIntentByNameResponse {
  case class Ok(value: Assign) extends GetIntentByNameResponse
  case object NotFound         extends GetIntentByNameResponse
}
sealed abstract class DeleteIntentResponse {
  def fold[A](handleOk: Assign => A): A = this match {
    case x: DeleteIntentResponse.Ok =>
      handleOk(x.value)
  }
}
object DeleteIntentResponse { case class Ok(value: Assign) extends DeleteIntentResponse }
