package com.github.artkostm.server.Event
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
trait EventHandler[F[_]] {
  def getEventByLink(respond: GetEventByLinkResponse.type)(link: String): F[GetEventByLinkResponse]
}
class EventResource[F[_]]()(implicit E: Effect[F]) extends Http4sDsl[F] {
  object GetEventByLinkLinkMatcher extends QueryParamDecoderMatcher[String]("link")
  val getEventByLinkOkEncoder = jsonEncoderOf[F, Info]
  def routes(handler: EventHandler[F]): HttpRoutes[F] = HttpRoutes.of {
    {
      case req @ GET -> Root / "info" / "" :? GetEventByLinkLinkMatcher(link) =>
        handler.getEventByLink(GetEventByLinkResponse)(link) flatMap {
          case GetEventByLinkResponse.Ok(value) =>
            Ok(value)(E, getEventByLinkOkEncoder)
          case GetEventByLinkResponse.NotFound =>
            NotFound()
        }
    }
  }
}
sealed abstract class GetEventByLinkResponse {
  def fold[A](handleOk: Info => A, handleNotFound: => A): A = this match {
    case x: GetEventByLinkResponse.Ok =>
      handleOk(x.value)
    case GetEventByLinkResponse.NotFound =>
      handleNotFound
  }
}
object GetEventByLinkResponse {
  case class Ok(value: Info) extends GetEventByLinkResponse
  case object NotFound       extends GetEventByLinkResponse
}
