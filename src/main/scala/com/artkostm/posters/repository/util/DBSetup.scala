package com.artkostm.posters.repository.util

import com.artkostm.posters.repository.{HasDatabaseConfig, PostersPgProfile}
import org.joda.time.DateTime
import slick.dbio.DBIOAction
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext

trait DBSetup { self: HasDatabaseConfig[PostersPgProfile] =>
  import profile.api._

  def setUp(query: TableQuery[Table[_]])(implicit ctx: ExecutionContext) = {
    val createIfNotExist = MTable.getTables.flatMap(v => {
      val names = v.map(_.name.name)
      if (!names.contains(query.baseTableRow.tableName)) query.schema.create
      else DBIOAction.successful()
    })
    DBIO.seq(createIfNotExist, query.filter().delete)
  }
}
