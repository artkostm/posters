package com.artkostm.posters.endpoint

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.{ChronoField, ChronoUnit, TemporalAccessor}

import cats.data.EitherT
import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.categories.CategoryVar
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.schedule.Category
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import org.http4s.{AuthedService, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl

class CategoryEndpoint[F[_]: Effect](repository: EventStore[F]) extends Http4sDsl[F] with EndpointsAware[F] {
  import com.artkostm.posters.jsoniter._
  import com.artkostm.posters.ValidationError._

  // TODO: move into package at root
  val FMT = new DateTimeFormatterBuilder()
    .appendPattern("yyyy/MM/dd")
    .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
    .toFormatter()
    .withZone(ZoneId.of("Europe/Minsk"))

  implicit val yearQueryParamDecoder: QueryParamDecoder[Instant] =
    QueryParamDecoder[String].map(
      FMT.parse(_, (temporal: TemporalAccessor) => Instant.from(temporal).truncatedTo(ChronoUnit.DAYS)))

  object DateMatcher extends QueryParamDecoderMatcher[Instant]("date")

  // TODO: move codecs to appropriate places
  implicit val categoryCodec: JsonValueCodec[Category] = JsonCodecMaker.make[Category](CodecMakerConfig())

  private def getCategoryByName(): AuthedService[User, F] = AuthedService {
    // TODO: add validation for the name (create enum and corresponding object with unapply)
    case GET -> Root / "categories" / CategoryVar(categoryName) :? DateMatcher(date) as _ =>
      for {
        category <- EitherT
                     .fromOptionF(repository.findByNameAndDate(categoryName.entryName, date),
                                  CategoryNotFound(s"Cannot find '$categoryName' category using date=$date"))
                     .value
        resp <- category.fold(NotFound(_), Ok(_))
      } yield resp
  }

  override def endpoints: AuthedService[User, F] = getCategoryByName()
}

object CategoryEndpoint {
  def apply[F[_]: Effect](repository: EventStore[F]): AuthedService[User, F] =
    new CategoryEndpoint(repository).endpoints
}
