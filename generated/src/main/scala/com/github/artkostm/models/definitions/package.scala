package com.github.artkostm.models
import java.time._
import io.circe.java8.time._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
object definitions$ {
  val guardrailDecodeInstant: Decoder[Instant] = Decoder[Instant].or(Decoder[Long].map(Instant.ofEpochMilli))
  val guardrailDecodeLocalDate: Decoder[LocalDate] =
    Decoder[LocalDate].or(Decoder[Instant].map(_.atZone(ZoneOffset.UTC).toLocalDate))
  val guardrailDecodeLocalDateTime: Decoder[LocalDateTime] = Decoder[LocalDateTime]
  val guardrailDecodeLocalTime: Decoder[LocalTime]         = Decoder[LocalTime]
  val guardrailDecodeOffsetDateTime: Decoder[OffsetDateTime] =
    Decoder[OffsetDateTime].or(Decoder[Instant].map(_.atZone(ZoneOffset.UTC).toOffsetDateTime))
  val guardrailDecodeZonedDateTime: Decoder[ZonedDateTime]   = Decoder[ZonedDateTime]
  val guardrailEncodeInstant: Encoder[Instant]               = Encoder[Instant]
  val guardrailEncodeLocalDate: Encoder[LocalDate]           = Encoder[LocalDate]
  val guardrailEncodeLocalDateTime: Encoder[LocalDateTime]   = Encoder[LocalDateTime]
  val guardrailEncodeLocalTime: Encoder[LocalTime]           = Encoder[LocalTime]
  val guardrailEncodeOffsetDateTime: Encoder[OffsetDateTime] = Encoder[OffsetDateTime]
  val guardrailEncodeZonedDateTime: Encoder[ZonedDateTime]   = Encoder[ZonedDateTime]
}
package object definitions {
  implicit val guardrailDecodeInstant: Decoder[Instant]               = definitions$.guardrailDecodeInstant
  implicit val guardrailDecodeLocalDate: Decoder[LocalDate]           = definitions$.guardrailDecodeLocalDate
  implicit val guardrailDecodeLocalDateTime: Decoder[LocalDateTime]   = definitions$.guardrailDecodeLocalDateTime
  implicit val guardrailDecodeLocalTime: Decoder[LocalTime]           = definitions$.guardrailDecodeLocalTime
  implicit val guardrailDecodeOffsetDateTime: Decoder[OffsetDateTime] = definitions$.guardrailDecodeOffsetDateTime
  implicit val guardrailDecodeZonedDateTime: Decoder[ZonedDateTime]   = definitions$.guardrailDecodeZonedDateTime
  implicit val guardrailEncodeInstant: Encoder[Instant]               = definitions$.guardrailEncodeInstant
  implicit val guardrailEncodeLocalDate: Encoder[LocalDate]           = definitions$.guardrailEncodeLocalDate
  implicit val guardrailEncodeLocalDateTime: Encoder[LocalDateTime]   = definitions$.guardrailEncodeLocalDateTime
  implicit val guardrailEncodeLocalTime: Encoder[LocalTime]           = definitions$.guardrailEncodeLocalTime
  implicit val guardrailEncodeOffsetDateTime: Encoder[OffsetDateTime] = definitions$.guardrailEncodeOffsetDateTime
  implicit val guardrailEncodeZonedDateTime: Encoder[ZonedDateTime]   = definitions$.guardrailEncodeZonedDateTime
}