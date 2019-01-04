package com.artkostm.posters.interfaces.schedule

import org.joda.time.DateTime

case class Media(link: String, img: String)

case class Description(desc: String, ticket: Option[String], isFree: Boolean)

case class Event(media: Media, name: String, description: Description)

case class Category(name: String, events: List[Event])

case class Day(events: List[Category], date: DateTime)
