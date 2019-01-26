package com.github.artkostm.server.Category
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
trait CategoryHandler[F[_]] {
  def getAllEventsByName(respond: GetAllEventsByNameResponse.type)(date: String,
                                                                   name: String): F[GetAllEventsByNameResponse]
  def getAllEvents(respond: GetAllEventsResponse.type)(date: String): F[GetAllEventsResponse]
}
class CategoryResource[F[_]]()(implicit E: Effect[F]) extends Http4sDsl[F] {
  object GetAllEventsByNameDateMatcher extends QueryParamDecoderMatcher[String]("date")
  object GetAllEventsByNameNameMatcher extends QueryParamDecoderMatcher[String]("name")
  val getAllEventsByNameOkEncoder = jsonEncoderOf[F, Category]
  val getAllEventsOkEncoder       = jsonEncoderOf[F, IndexedSeq[BigDecimal]]
  def routes(handler: CategoryHandler[F]): HttpRoutes[F] = HttpRoutes.of {
    {
      case req @ GET -> Root / "posters" / "" :? GetAllEventsByNameDateMatcher(date) +& GetAllEventsByNameNameMatcher(
            name) =>
        handler.getAllEventsByName(GetAllEventsByNameResponse)(date, name) flatMap {
          case GetAllEventsByNameResponse.Ok(value) =>
            Ok(value)(E, getAllEventsByNameOkEncoder)
          case GetAllEventsByNameResponse.NotFound =>
            NotFound()
        }
      case req @ GET -> Root / "posters" / date / "" =>
        handler.getAllEvents(GetAllEventsResponse)(date) flatMap {
          case GetAllEventsResponse.Ok(value) =>
            Ok(value)(E, getAllEventsOkEncoder)
          case GetAllEventsResponse.NotFound =>
            NotFound()
        }
    }
  }
}
sealed abstract class GetAllEventsByNameResponse {
  def fold[A](handleOk: Category => A, handleNotFound: => A): A = this match {
    case x: GetAllEventsByNameResponse.Ok =>
      handleOk(x.value)
    case GetAllEventsByNameResponse.NotFound =>
      handleNotFound
  }
}
object GetAllEventsByNameResponse {
  case class Ok(value: Category) extends GetAllEventsByNameResponse
  case object NotFound           extends GetAllEventsByNameResponse
}
sealed abstract class GetAllEventsResponse {
  def fold[A](handleOk: IndexedSeq[BigDecimal] => A, handleNotFound: => A): A = this match {
    case x: GetAllEventsResponse.Ok =>
      handleOk(x.value)
    case GetAllEventsResponse.NotFound =>
      handleNotFound
  }
}
object GetAllEventsResponse {
  case class Ok(value: IndexedSeq[BigDecimal]) extends GetAllEventsResponse
  case object NotFound                         extends GetAllEventsResponse
}
