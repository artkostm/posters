package com.artkostm.posters.model

import org.joda.time.DateTime
import play.api.libs.json.JsValue

case class Assign(date: DateTime, eventName: String, vIds: List[String] = List.empty, uIds: List[String] = List.empty)

case class EventsDay(date: DateTime, categories: JsValue)

case class Info(link: String, eventInfo: EventInfo)
