package com.artkostm.posters.postgres

import java.util.Date

import com.artkostm.posters.model.Events
import slick.dbio.DBIOAction
import slick.jdbc.H2Profile
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

class EventPersister(db: H2Profile.backend.Database) {
  def setUp: Future[Unit] = db.run(EventPersister.setupAction)
}

object EventPersister {
  import scala.concurrent.ExecutionContext.Implicits.global
  private[this] implicit val d2t = com.artkostm.posters.model.DateMapper.DateTime2SqlTimestampMapper
  val events: TableQuery[Events] = TableQuery[Events]

  private[this] val createIfNotExist = MTable.getTables.flatMap(v => {
    val names = v.map(_.name.name)
    if (!names.contains(events.baseTableRow.tableName)) events.schema.create
    else DBIOAction.from(Future.successful())
  })

  private[this] val deleteOldEvents: Query[Events, (String, Date, String, String), Seq] = events.filter(event => event.date < new Date())

  val setupAction: DBIO[Unit] = DBIO.seq(createIfNotExist, deleteOldEvents.delete)

  def saveAction(category: String, date: Date, eventName: String, ids: String): DBIO[Int] = events.insertOrUpdate((category, date, eventName, ids))

  def get = for { e <- events } yield e
}
