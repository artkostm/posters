package com.artkostm.posters.repository

import com.artkostm.posters.model.InfoTable
import slick.dbio.DBIOAction
import slick.jdbc.meta.MTable

import scala.concurrent.{ExecutionContext, Future}

trait InfoRepository extends InfoTable { this: JsonSupportDbComponent =>
  import driver.plainAPI._

  def setUpInfo()(implicit ctx: ExecutionContext): Future[Unit] = db.run {
    val createIfNotExist = MTable.getTables.flatMap(v => {
      val names = v.map(_.name.name)
      if (!names.contains(infoTableQuery.baseTableRow.tableName)) infoTableQuery.schema.create
      else DBIOAction.successful()
    })
    DBIO.seq(createIfNotExist)
  }
}
