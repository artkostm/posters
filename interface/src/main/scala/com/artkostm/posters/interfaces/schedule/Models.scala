package com.artkostm.posters.interfaces.schedule

import java.time.LocalDate

final case class Media(link: String, img: String)

final case class Description(desc: String, ticket: Option[String], isFree: Boolean)

final case class Event(name: String, media: Media, description: Description)

final case class Category(name: String, events: List[Event])

final case class Day(eventDate: LocalDate, categories: List[Category])
