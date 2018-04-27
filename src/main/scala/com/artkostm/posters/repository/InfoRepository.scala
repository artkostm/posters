package com.artkostm.posters.repository

import com.artkostm.posters.model.{EventInfo, Info}
import com.artkostm.posters.repository.util.DBSetupOps
import slick.dbio.DBIOAction
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

trait InfoTable { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  private[InfoTable] class Information(tag: Tag) extends Table[Info](tag, "info") {
    def link: Rep[String] = column[String]("link")
    def eventsInfo: Rep[EventInfo] = column[EventInfo]("eventsInfo")

    def * : ProvenShape[Info] = (link, eventsInfo) <> (Info.tupled, Info.unapply)
    def pk = primaryKey("pk_info", link)
  }

  protected val Information = TableQuery[Information]
}

trait InfoRepository extends InfoTable with DBSetupOps { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  def setUpInfo()(implicit ctx: ExecutionContext) =
    setUp(Information, DBIOAction.successful())

  def saveInfo(info: Info): Future[Int] = db.run(Information.insertOrUpdate(info))

  def findInfo(link: String): Future[Option[Info]] =
    db.run(Information.filter(i => i.link === link).result.headOption)
}
