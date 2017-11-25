package com.artkostm.posters.repository

import com.artkostm.posters.model.{Assign, EventsTable}
import org.joda.time.DateTime
import slick.dbio.DBIOAction
import slick.jdbc.meta.MTable

import scala.concurrent.{ExecutionContext, Future}

trait EventsRepository extends EventsTable { this: DbComponent =>
  import driver.api._

  def setUp()(implicit ctx: ExecutionContext): Future[Unit] = db.run {
    val createIfNotExist = MTable.getTables.flatMap(v => {
      val names = v.map(_.name.name)
      if (!names.contains(eventsTableQuery.baseTableRow.tableName)) eventsTableQuery.schema.create
      else DBIOAction.successful()
    })
    DBIO.seq(createIfNotExist, eventsTableQuery.filter(event => event.date < DateTime.now.minusDays(1)).delete)
  }

  def save(assign: Assign): Future[Int] = db.run(eventsTableQuery.insertOrUpdate(assign))

  def all: Future[List[Assign]] = db.run(eventsTableQuery.to[List].result)

  def find(category: String, date: DateTime, eventName: String): Future[Option[Assign]] = db.run {
    eventsTableQuery.filter(a => a.category === category && a.eventName === eventName && a.date === date).result.headOption
  }
}
