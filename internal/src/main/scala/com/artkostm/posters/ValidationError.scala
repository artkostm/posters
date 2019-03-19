package com.artkostm.posters

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}

sealed trait ValidationError extends Exception {
  def code: Int
  def msg: String
}

object ValidationError {
  final case class ApiError(msg: String, code: Int) extends ValidationError


  implicit val apiErrorCodec: JsonValueCodec[ApiError] =
    JsonCodecMaker.make[ApiError](CodecMakerConfig())
}