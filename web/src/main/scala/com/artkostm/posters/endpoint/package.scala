package com.artkostm.posters

import java.time.Instant
import java.time.temporal.{ChronoUnit, TemporalAccessor}

import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.{QueryParamDecoderMatcher, ValidatingQueryParamDecoderMatcher}

package object endpoint {
  /* Parses out date query param in format yyyy/MM/dd */
  object DateMatcher extends ValidatingQueryParamDecoderMatcher[Instant]("date")
  /* Parses out link query param */
  object LinkMatcher extends QueryParamDecoderMatcher[String]("link")

  implicit val yearQueryParamDecoder: QueryParamDecoder[Instant] =
    QueryParamDecoder[String].map(
      FMT.parse(_, (temporal: TemporalAccessor) => Instant.from(temporal).truncatedTo(ChronoUnit.DAYS)))
}
