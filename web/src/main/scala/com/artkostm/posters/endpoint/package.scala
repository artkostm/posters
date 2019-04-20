package com.artkostm.posters

import java.time.LocalDate
import java.time.temporal.TemporalAccessor

import com.artkostm.posters.interfaces.dialog.v1.Period
import com.github.plokhotnyuk.jsoniter_scala.core.{JsonReader, JsonValueCodec, JsonWriter}
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.QueryParamDecoderMatcher

import scala.runtime.Nothing$

package object endpoint {
  /* Parses out date query param in format yyyy/MM/dd */
  object DateMatcher extends QueryParamDecoderMatcher[LocalDate]("date")
  /* Parses out link query param */
  object LinkMatcher extends QueryParamDecoderMatcher[String]("link")
  /* Parses out eventName query param */
  object EventNameMatcher extends QueryParamDecoderMatcher[String]("eventName")

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

  implicit val periodV1JsonValueCodec = new JsonValueCodec[Period] {
    override def decodeValue(in: JsonReader, default: Period): Period = {
      in.readString("").split("/") match {
        case Array(start, end) =>
          if (start > end) in.readNullOrError(null, "Incorrect Period format, start of period can't be after end of period")
          else Period(LocalDate.parse(start), LocalDate.parse(end))
        case _ => in.readNullOrError(null, "Incorrect Period format, should be yyyy-MM-dd/yyyy-MM-dd")
      }
    }

    override def encodeValue(x: Period, out: JsonWriter): Unit =
      out.writeVal(s"${x.startDate}/${x.endDate}")

    override def nullValue: Period = null
  }
}
