package com.artkostm.posters.model


import com.artkostm.posters.repository.JsonSupportDbComponent
import org.joda.time.DateTime
import play.api.libs.json.JsValue
import slick.lifted.ProvenShape

case class Assign(category: String, date: DateTime, eventName: String, ids: String)

case class EventsDay(date: DateTime, categories: List[Category])

case class Info(link: String, eventInfo: EventInfo)


case class Test(id: String, data: JsValue)

trait TestTable { this: JsonSupportDbComponent =>
  import driver.api._

  class Tests(tag: Tag) extends Table[Test](tag, "test") {
    def id: Rep[String] = column[String]("id")
    def data: Rep[JsValue] = column[JsValue]("data")

    def * : ProvenShape[Test] = (id, data) <> (Test.tupled, Test.unapply)
  }

  protected val testQuery = TableQuery[Tests]
}