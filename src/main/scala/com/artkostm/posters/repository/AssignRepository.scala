package com.artkostm.posters.repository

import com.artkostm.posters.model.{Assign, AssignTable}
import org.joda.time.DateTime
import slick.dbio.DBIOAction
import slick.jdbc.meta.MTable

import scala.concurrent.{ExecutionContext, Future}

trait AssignRepository extends AssignTable { this: DbComponent =>
  import driver.api._

  def setUpAssign()(implicit ctx: ExecutionContext): Future[Unit] = db.run {
    val createIfNotExist = MTable.getTables.flatMap(v => {
      val names = v.map(_.name.name)
      if (!names.contains(assigneesTableQuery.baseTableRow.tableName)) assigneesTableQuery.schema.create
      else DBIOAction.successful()
    })
    DBIO.seq(createIfNotExist, assigneesTableQuery.filter(event => event.date < DateTime.now.minusDays(1)).delete)
  }

  def save(assign: Assign) = db.run(
    assigneesTableQuery.returning(assigneesTableQuery).insertOrUpdate(assign.copy(date = assign.date.withTimeAtStartOfDay())).transactionally
  )

  def all: Future[List[Assign]] = db.run(assigneesTableQuery.to[List].result)

  def find(category: String, date: DateTime, eventName: String): Future[Option[Assign]] = db.run {
    assigneesTableQuery.filter(a => a.category === category &&
      a.eventName === eventName &&
      a.date === date.withTimeAtStartOfDay()).result.headOption
  }
}
