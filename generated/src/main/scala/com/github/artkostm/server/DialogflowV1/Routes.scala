package com.github.artkostm.server.DialogflowV1
import cats.data.EitherT
import cats.implicits._
import cats.effect.IO
import cats.effect.Effect
import org.http4s.{ Status => _, _ }
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
trait DialogflowV1Handler[F[_]] { def getAllEventsByCatNameAndDate(respond: GetAllEventsByCatNameAndDateResponse.type)(body: Option[DialogflowRequestBody] = None): F[GetAllEventsByCatNameAndDateResponse] }
class DialogflowV1Resource[F[_]]()(implicit E: Effect[F]) extends Http4sDsl[F] {
  val getAllEventsByCatNameAndDateDecoder: EntityDecoder[F, Option[DialogflowRequestBody]] = decodeBy(MediaType.text.plain) {
    msg => msg.contentLength.filter(_ > 0).fold[DecodeResult[F, Option[DialogflowRequestBody]]](DecodeResult.success(None)) {
      _ => DecodeResult.success(decodeString(msg)).flatMap {
        str => Json.fromString(str).as[Option[DialogflowRequestBody]].fold(failure => DecodeResult.failure(InvalidMessageBodyFailure(s"Could not decode response: $str", Some(failure))), DecodeResult.success(_))
      }
    }
  }
  val getAllEventsByCatNameAndDateOkEncoder = jsonEncoderOf[F, DialogflowResponse]
  def routes(handler: DialogflowV1Handler[F]): HttpRoutes[F] = HttpRoutes.of {
    {
      case req @ POST -> Root / "webhook" / "v1" / "" =>
        req.decodeWith(getAllEventsByCatNameAndDateDecoder, strict = false) { body => 
          handler.getAllEventsByCatNameAndDate(GetAllEventsByCatNameAndDateResponse)(body) flatMap {
            case GetAllEventsByCatNameAndDateResponse.Ok(value) =>
              Ok(value)(E, getAllEventsByCatNameAndDateOkEncoder)
            case GetAllEventsByCatNameAndDateResponse.BadRequest =>
              BadRequest()
            case GetAllEventsByCatNameAndDateResponse.NotFound =>
              NotFound()
          }
        }
    }
  }
}
sealed abstract class GetAllEventsByCatNameAndDateResponse {
  def fold[A](handleOk: DialogflowResponse => A, handleBadRequest: => A, handleNotFound: => A): A = this match {
    case x: GetAllEventsByCatNameAndDateResponse.Ok =>
      handleOk(x.value)
    case GetAllEventsByCatNameAndDateResponse.BadRequest =>
      handleBadRequest
    case GetAllEventsByCatNameAndDateResponse.NotFound =>
      handleNotFound
  }
}
object GetAllEventsByCatNameAndDateResponse {
  case class Ok(value: DialogflowResponse) extends GetAllEventsByCatNameAndDateResponse
  case object BadRequest extends GetAllEventsByCatNameAndDateResponse
  case object NotFound extends GetAllEventsByCatNameAndDateResponse
}