package com.artkostm.posters.endpoint
import java.time.{Instant, ZoneId}
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.{ChronoField, TemporalAccessor, TemporalQuery}
import java.util.Locale

import cats.data.EitherT
import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.CategoryNotFound
import com.artkostm.posters.algebra.EventStore
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.schedule.Category
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import org.http4s.{AuthedService, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl

class CategoryEndpoint[F[_]: Effect](repository: EventStore[F]) extends Http4sDsl[F] with EndpointsAware[F] {
  import com.artkostm.posters.jsoniter._

  val FMT = new DateTimeFormatterBuilder()
    .appendPattern("yyyy/MM/dd")
    .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
    .toFormatter()
    .withZone(ZoneId.of("Europe/Minsk"))

  implicit val yearQueryParamDecoder: QueryParamDecoder[Instant] =
    QueryParamDecoder[String].map(FMT.parse(_, (temporal: TemporalAccessor) => Instant.from(temporal)))

  object DateMatcher extends QueryParamDecoderMatcher[Instant]("date")

  implicit val categoryCodec: JsonValueCodec[Category] = JsonCodecMaker.make[Category](CodecMakerConfig())

  implicit val categoryNotFoundCodec: JsonValueCodec[CategoryNotFound] =
    JsonCodecMaker.make[CategoryNotFound](CodecMakerConfig())

  private def getCategoryByName(): AuthedService[User, F] = AuthedService {
    case GET -> Root / "categories" / name :? DateMatcher(date) as _ =>
      println(name)
      for {
        category <- EitherT
                     .fromOptionF(repository.findByNameAndDate(name, date),
                                  CategoryNotFound(s"Cannot find '$name' category using date=$date"))
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
