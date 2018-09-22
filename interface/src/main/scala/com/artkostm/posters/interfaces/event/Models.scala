package com.artkostm.posters.interfaces.event

case class Comment(author: String, date: String, text: String)

case class EventInfo(description: String, photos: List[String], comments: List[Comment])
