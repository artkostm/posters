package com.artkostm.posters.model

import java.util.Date

case class Media(link: String, img: String)
case class Description(desc: String, ticket: Option[String], isFree: Boolean)
case class Event(media: Media, name: String, description: Description)
case class Category(name: String, events: List[Event])

sealed trait Schedule
case class Day(events: List[Category], date: Date) extends Schedule
