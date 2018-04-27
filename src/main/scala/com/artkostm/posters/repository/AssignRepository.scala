package com.artkostm.posters.repository

import com.artkostm.posters.model.Assign
import com.artkostm.posters.repository.util.DBSetupOps
import org.joda.time.DateTime
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

trait AssignTable { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  private[AssignTable] class Assignees(tag: Tag) extends Table[Assign](tag, "assignees") {

    def category: Rep[String] = column[String]("category")
    def date: Rep[DateTime] = column[DateTime]("date")
    def eventName: Rep[String] = column[String]("event_name")
    def ids: Rep[String] = column[String]("ids")

    def * : ProvenShape[Assign] = (category, date, eventName, ids) <> (Assign.tupled, Assign.unapply)
    def pk = primaryKey("pk_events", (category, date, eventName))
  }

  protected val Assignees = TableQuery[Assignees]
}


trait AssignRepository extends AssignTable with DBSetupOps { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  def setUpAssign()(implicit ctx: ExecutionContext) =
    setUp(Assignees, Assignees.filter(event => event.date < DateTime.now.minusDays(1)).delete)

  def saveAssign(assign: Assign) =
    db.run(Assignees.insertOrUpdate(assign.copy(date = assign.date.withTimeAtStartOfDay())).transactionally)

  def allAssignees: Future[List[Assign]] = db.run(Assignees.to[List].result)

  def findAssign(category: String, date: DateTime, eventName: String): Future[Option[Assign]] =
    db.run { Assignees.filter(a =>
      a.category === category
        && a.eventName === eventName
        && a.date === date.withTimeAtStartOfDay()
    ).result.headOption }
}
