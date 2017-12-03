package com.artkostm.posters.model

import java.util.Date

case class Media(link: String, img: String)
case class Description(desc: String, ticket: Option[String], isFree: Boolean)
case class Event(media: Media, name: String, description: Description)
case class Category(name: String, events: List[Event])

sealed trait Schedule
case class Day(events: List[Category], date: Date) extends Schedule

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

  implicit val dayFmt = Json.format[Day]
  implicit val dayWrt = Json.writes[Day]
}