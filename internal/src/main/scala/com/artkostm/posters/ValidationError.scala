package com.artkostm.posters

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}

sealed trait ValidationError {
  def code: Int
  def msg: String
}

object ValidationError {
  final case class EventInfoNotFound(msg: String, code: Int = 404) extends ValidationError
  final case class CategoryNotFound(msg: String, code: Int = 404) extends ValidationError


  implicit val eventInfoNotFoundCodec: JsonValueCodec[EventInfoNotFound] =
    JsonCodecMaker.make[EventInfoNotFound](CodecMakerConfig())

  implicit val categoryNotFoundCodec: JsonValueCodec[CategoryNotFound] =
    JsonCodecMaker.make[CategoryNotFound](CodecMakerConfig())
}