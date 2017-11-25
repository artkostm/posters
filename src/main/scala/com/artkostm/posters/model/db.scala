package com.artkostm.posters.model


import com.artkostm.posters.repository.DbComponent
import org.joda.time.DateTime
import slick.lifted.ProvenShape

trait EventsTable { this: DbComponent =>
  import driver.api._
  protected implicit val DateTime2SqlTimestampMapper = MappedColumnType.base[DateTime, java.sql.Timestamp]({
    date => new java.sql.Timestamp(date.toDate.getTime)
  }, {
    sqlTimestamp => new DateTime(sqlTimestamp.getTime())
  })

  private[EventsTable] class Events(tag: Tag) extends Table[Assign](tag, "events") {
    def category: Rep[String] = column[String]("category")
    def date: Rep[DateTime] = column[DateTime]("date")
    def eventName: Rep[String] = column[String]("event_name")
    def ids: Rep[String] = column[String]("ids")

    def * : ProvenShape[Assign] = (category, date, eventName, ids) <> (Assign.tupled, Assign.unapply)
    def pk = primaryKey("pk_events", (category, date, eventName))
  }

  protected val eventsTableQuery = TableQuery[Events]
}

case class Assign(category: String, date: DateTime, eventName: String, ids: String)
