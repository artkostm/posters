package com.artkostm.posters.interfaces.event

case class Comment(author: String, date: String, text: String, rating: Option[String] = None)

case class EventInfo(description: String, photos: List[String], comments: List[Comment])
