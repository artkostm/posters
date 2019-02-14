package com.artkostm.posters

sealed trait ValidationError {
  def code: Int
  def msg: String
}

final case class EventInfoNotFound(msg: String, code: Int = 404) extends ValidationError