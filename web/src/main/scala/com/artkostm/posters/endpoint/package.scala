package com.artkostm.posters

import java.time.LocalDate
import java.time.temporal.TemporalAccessor

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
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

  val ApiVersion = "v1"

  case class ApiErrorCode(value: Int) extends AnyVal

  object ApiErrorCode {
    val ENTITY_NOT_FOUND = ApiErrorCode(100)
    val SAME_COUNTRIES_SEARCH = ApiErrorCode(101)
  }

  final case class ApiError(msg: String, code: Int)

  implicit val apiErrorCodec: JsonValueCodec[ApiError] =
    JsonCodecMaker.make[ApiError](CodecMakerConfig())
}
