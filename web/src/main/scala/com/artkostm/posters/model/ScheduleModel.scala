package com.artkostm.posters.model

import org.joda.time.DateTime

case class Media(link: String, img: String)
case class Description(desc: String, ticket: Option[String], isFree: Boolean)
case class Event(media: Media, name: String, description: Description)
case class Category(name: String, events: List[Event])

sealed trait Schedule
case class Day(events: List[Category], date: DateTime) extends Schedule

object Category {
  import play.api.libs.json._
  implicit val descriptionFmt = Json.format[Description]
  implicit val descriptionWrt = Json.writes[Description]

  implicit val mediaFmt = Json.format[Media]
  implicit val mediaWrt = Json.writes[Media]

  implicit val evnFmt = Json.format[Event]
  implicit val evnWrt = Json.writes[Event]

  implicit val catFmt = Json.format[Category]
  implicit val catWrt = Json.writes[Category]

  def toCategory(json: JsValue): Category           = json.as[Category]
  def toCategoryList(json: JsValue): List[Category] = json.as[List[Category]]
  def toJson(categories: List[Category]): JsValue   = Json.toJson(categories)
}
