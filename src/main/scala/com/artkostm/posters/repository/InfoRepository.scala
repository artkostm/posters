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

  protected val Info = TableQuery[Information]
}

trait InfoRepository extends InfoTable with DBSetupOps { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  def setUpInfo()(implicit ctx: ExecutionContext) =
    setUp(Info, DBIOAction.successful())

  def save(info: Info): Future[Int] = db.run(Info.insertOrUpdate(info))

  def find(link: String): Future[Option[Info]] = db.run {
    Info.filter(i => i.link === link).result.headOption
  }
}
