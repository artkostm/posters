package com.artkostm.posters.model

case class Comment(author: String, date: String, text: String)
case class EventInfo(description: String, photos: List[String], comments: List[Comment])

object EventInfo {
  import play.api.libs.json._
  implicit val commentFmt = Json.format[Comment]
  implicit val commentWrt = Json.writes[Comment]

  implicit val eventInfoFmt = Json.format[EventInfo]
  implicit val eventInfoWrt = Json.writes[EventInfo]
}