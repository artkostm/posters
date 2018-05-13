package com.artkostm.posters.repository

import com.artkostm.posters.model.Assign
import com.artkostm.posters.repository.util.DBSetupOps
import org.joda.time.DateTime
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

trait AssignTable { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  private[AssignTable] class Assignees(tag: Tag) extends Table[Assign](tag, "assignees") {

    def date: Rep[DateTime] = column[DateTime]("date")
    def eventName: Rep[String] = column[String]("event_name")
    def vIds: Rep[List[String]] = column[List[String]]("vids")// TODO: sould be pgarray
    def uIds: Rep[List[String]] = column[List[String]]("uids")// TODO: sould be pgarray

    def * : ProvenShape[Assign] = (date, eventName, vIds, uIds) <> (Assign.tupled, Assign.unapply)
    def pk = primaryKey("pk_events", (date, eventName))
  }

  protected val Assignees = TableQuery[Assignees]
}


trait AssignRepository extends AssignTable with DBSetupOps { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  val compiledAssignByDateAndName = Compiled { (date: Rep[DateTime], name: Rep[String]) =>
    Assignees.filter(a => a.eventName === name && a.date === date)
  }

  def setUpAssign()(implicit ctx: ExecutionContext) =
    setUp(Assignees, Assignees.filter(event => event.date < DateTime.now.minusDays(1)).delete)

  def saveAssign(isUser: Boolean, date: DateTime, eventName: String, id: String)(implicit ctx: ExecutionContext) =
    db.run {
      val target = Assignees.filter(a => a.eventName === eventName && a.date === date.withTimeAtStartOfDay())
        .map(a => if (isUser) a.uIds else a.vIds)
      (for {
        idsOpt <- target.result.headOption
        updateActionOption = idsOpt.map(ids => target.update(ids.::(id)))
        affected <- updateActionOption.getOrElse(
          Assignees += Assign(date, eventName, if (isUser) List.empty else List(id), if (isUser) List(id) else List.empty)
        )
      } yield affected) transactionally
    }

  def removeAssign(isUser: Boolean, date: DateTime, eventName: String, id: String)(implicit ctx: ExecutionContext) =
    db.run {
      val target = Assignees.filter(a => a.eventName === eventName && a.date === date.withTimeAtStartOfDay())
        .map(a => if (isUser) a.uIds else a.vIds)
      (for {
        idsOpt <- target.result.headOption
        updateActionOption = idsOpt.map(ids => target.update(ids.filter(_ != id)))
        affected <- updateActionOption.getOrElse(
          Assignees += Assign(date, eventName, if (isUser) List.empty else List(id), if (isUser) List(id) else List.empty)
        )
      } yield affected) transactionally
    }

  def allAssignees: Future[List[Assign]] = db.run(Assignees.to[List].result)

  def findAssign(date: DateTime, eventName: String): Future[Option[Assign]] =
    db.run(compiledAssignByDateAndName(date.withTimeAtStartOfDay(), eventName).result.headOption)
}
