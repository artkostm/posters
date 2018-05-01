package com.artkostm.posters.model

import org.joda.time.DateTime
import play.api.libs.json.JsValue

case class Assign(category: String, date: DateTime, eventName: String, ids: String)

case class EventsDay(date: DateTime, categories: JsValue)

case class Info(link: String, eventInfo: EventInfo)
