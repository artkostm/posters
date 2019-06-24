package com.artkostm.posters.interfaces

package object event {
  final case class Comment(author: String, date: String, text: String, rating: Option[String] = None)

  final case class EventData(description: String, photos: List[String], comments: List[Comment])

  final case class EventInfo(link: String, eventInfo: EventData)
}
