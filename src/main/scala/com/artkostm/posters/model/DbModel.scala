package com.artkostm.posters.model


import com.artkostm.posters.repository.{JsonSupportDbComponent, DbComponent}
import org.joda.time.DateTime
import slick.lifted.ProvenShape

trait AssignTable { this: DbComponent =>
  import driver.api._
  protected implicit val DateTime2SqlTimestampMapper = MappedColumnType.base[DateTime, java.sql.Timestamp]({
    date => new java.sql.Timestamp(date.toDate.getTime)
  }, {
    sqlTimestamp => new DateTime(sqlTimestamp.getTime())
  })

  private[AssignTable] class Assignees(tag: Tag) extends Table[Assign](tag, "assignees") {
    def category: Rep[String] = column[String]("category")
    def date: Rep[DateTime] = column[DateTime]("date")
    def eventName: Rep[String] = column[String]("event_name")
    def ids: Rep[String] = column[String]("ids")

    def * : ProvenShape[Assign] = (category, date, eventName, ids) <> (Assign.tupled, Assign.unapply)
    def pk = primaryKey("pk_events", (category, date, eventName))
  }

  protected val assigneesTableQuery = TableQuery[Assignees]
}

trait EventsDayTable { this: JsonSupportDbComponent =>
  import driver.plainAPI._

  private[EventsDayTable] class Days(tag: Tag) extends Table[EventsDay](tag, "days") {
    def date: Rep[DateTime] = column[DateTime]("date")
    def categories: Rep[List[Category]] = column[List[Category]]("categories")

    def * : ProvenShape[EventsDay] = (date, categories) <> (EventsDay.tupled, EventsDay.unapply)
    def pk = primaryKey("pk_days", date)
  }

  protected val daysTableQuery = TableQuery[Days]
}

trait InfoTable { this: JsonSupportDbComponent =>
  import driver.plainAPI._

  private[InfoTable] class Information(tag: Tag) extends Table[Info](tag, "info") {
    def link: Rep[String] = column[String]("link")
    def eventsInfo: Rep[EventInfo] = column[EventInfo]("eventsInfo")

    def * : ProvenShape[Info] = (link, eventsInfo) <> (Info.tupled, Info.unapply)
    def pk = primaryKey("pk_info", link)
  }

  protected val infoTableQuery = TableQuery[Information]
}

case class Assign(category: String, date: DateTime, eventName: String, ids: String)
case class EventsDay(date: DateTime, categories: List[Category])
case class Info(link: String, eventInfo: EventInfo)
