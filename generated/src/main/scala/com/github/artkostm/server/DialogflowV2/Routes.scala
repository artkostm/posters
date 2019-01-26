package com.github.artkostm.server.DialogflowV2
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
trait DialogflowV2Handler[F[_]] { def getAllEventsByNameAndDate(respond: GetAllEventsByNameAndDateResponse.type)(body: Option[DialogflowRequestBody] = None): F[GetAllEventsByNameAndDateResponse] }
class DialogflowV2Resource[F[_]]()(implicit E: Effect[F]) extends Http4sDsl[F] {
  val getAllEventsByNameAndDateDecoder: EntityDecoder[F, Option[DialogflowRequestBody]] = decodeBy(MediaType.text.plain) {
    msg => msg.contentLength.filter(_ > 0).fold[DecodeResult[F, Option[DialogflowRequestBody]]](DecodeResult.success(None)) {
      _ => DecodeResult.success(decodeString(msg)).flatMap {
        str => Json.fromString(str).as[Option[DialogflowRequestBody]].fold(failure => DecodeResult.failure(InvalidMessageBodyFailure(s"Could not decode response: $str", Some(failure))), DecodeResult.success(_))
      }
    }
  }
  val getAllEventsByNameAndDateOkEncoder = jsonEncoderOf[F, DialogflowResponse]
  def routes(handler: DialogflowV2Handler[F]): HttpRoutes[F] = HttpRoutes.of {
    {
      case req @ POST -> Root / "webhook" / "v2" / "" =>
        req.decodeWith(getAllEventsByNameAndDateDecoder, strict = false) { body => 
          handler.getAllEventsByNameAndDate(GetAllEventsByNameAndDateResponse)(body) flatMap {
            case GetAllEventsByNameAndDateResponse.Ok(value) =>
              Ok(value)(E, getAllEventsByNameAndDateOkEncoder)
            case GetAllEventsByNameAndDateResponse.BadRequest =>
              BadRequest()
            case GetAllEventsByNameAndDateResponse.NotFound =>
              NotFound()
          }
        }
    }
  }
}
sealed abstract class GetAllEventsByNameAndDateResponse {
  def fold[A](handleOk: DialogflowResponse => A, handleBadRequest: => A, handleNotFound: => A): A = this match {
    case x: GetAllEventsByNameAndDateResponse.Ok =>
      handleOk(x.value)
    case GetAllEventsByNameAndDateResponse.BadRequest =>
      handleBadRequest
    case GetAllEventsByNameAndDateResponse.NotFound =>
      handleNotFound
  }
}
object GetAllEventsByNameAndDateResponse {
  case class Ok(value: DialogflowResponse) extends GetAllEventsByNameAndDateResponse
  case object BadRequest extends GetAllEventsByNameAndDateResponse
  case object NotFound extends GetAllEventsByNameAndDateResponse
}