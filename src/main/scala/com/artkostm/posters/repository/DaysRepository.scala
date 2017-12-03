package com.artkostm.posters.repository

import com.artkostm.posters.model.{EventsDay, EventsDayTable}
import org.joda.time.DateTime
import slick.dbio.DBIOAction
import slick.jdbc.meta.MTable

import scala.concurrent.{ExecutionContext, Future}

trait DaysRepository extends EventsDayTable { this: JsonSupportDbComponent =>
  import driver.plainAPI._

  def setUpDays()(implicit ctx: ExecutionContext): Future[Unit] = db.run {
    val createIfNotExist = MTable.getTables.flatMap(v => {
      val names = v.map(_.name.name)
      if (!names.contains(daysTableQuery.baseTableRow.tableName)) daysTableQuery.schema.create
      else DBIOAction.successful()
    })
    DBIO.seq(createIfNotExist, daysTableQuery.filter(day => day.date < DateTime.now.minusDays(1)).delete)
  }

  def save(day: EventsDay): Future[Int] = db.run(daysTableQuery.insertOrUpdate(day.copy(date = day.date.withTimeAtStartOfDay())))

  def find(date: DateTime): Future[Option[EventsDay]] = db.run {
    daysTableQuery.filter(d => d.date === date.withTimeAtStartOfDay()).result.headOption
  }
}
