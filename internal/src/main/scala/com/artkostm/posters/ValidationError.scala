package com.artkostm.posters

import java.time.LocalDate

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}

sealed trait ValidationError extends Throwable

object ValidationError {
  final case class CategoryNotFoundError(name: String, date: LocalDate) extends ValidationError
  final case class CategoriesNotFoundError(date: LocalDate) extends ValidationError
  final case class EventInfoNotFoundError(link: String) extends ValidationError
  final case class RoleDoesNotExistError(role: String) extends ValidationError
  final case class IntentDoesNotExistError(eventName: String, date: LocalDate) extends ValidationError

  final case class ApiError(msg: String, code: Int)

  implicit val apiErrorCodec: JsonValueCodec[ApiError] =
    JsonCodecMaker.make[ApiError](CodecMakerConfig())
}
