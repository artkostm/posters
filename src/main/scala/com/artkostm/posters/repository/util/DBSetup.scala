package com.artkostm.posters.repository.util

import com.artkostm.posters.repository.{HasDatabaseConfig, PostersPgProfile}
import slick.dbio.{DBIOAction, DatabaseAction}
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext

trait DBSetupOps { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  def setUp[E<: Table[_]](query: TableQuery[E], deleteAction: DatabaseAction[Int, NoStream, Effect.Write])(implicit ctx: ExecutionContext) = {
    val createIfNotExist = MTable.getTables.flatMap(v => {
      val names = v.map(_.name.name)
      if (!names.contains(query.baseTableRow.tableName)) query.schema.create
      else DBIOAction.successful()
    })
    DBIO.seq(createIfNotExist, deleteAction)
  }
}
