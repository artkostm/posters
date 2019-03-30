package com.artkostm.posters

import java.time.LocalDate
import java.time.temporal.TemporalAccessor

import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.QueryParamDecoderMatcher

package object endpoint {
  /* Parses out date query param in format yyyy/MM/dd */
  object DateMatcher extends QueryParamDecoderMatcher[LocalDate]("date")
  /* Parses out link query param */
  object LinkMatcher extends QueryParamDecoderMatcher[String]("link")

  implicit val yearQueryParamDecoder: QueryParamDecoder[LocalDate] =
    QueryParamDecoder.fromUnsafeCast { qpv =>
      FMT.parse(qpv.value, (temporal: TemporalAccessor) => LocalDate.from(temporal))
    }("LocalDate")
}
