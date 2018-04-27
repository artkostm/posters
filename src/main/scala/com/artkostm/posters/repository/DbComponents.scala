package com.artkostm.posters.repository

import javax.sql.DataSource
import slick.basic.BasicProfile

sealed trait DbComponent[P <: BasicProfile] {
  protected[repository] val driver: P

  protected[repository] val db: P#Backend#Database
}

trait JsonSupportDbComponent extends DbComponent[PostersPgProfile] {
  protected def dataSource: DataSource
  protected override lazy val driver = PostersPgProfile

  import driver.api._
  override lazy val db = Database.forDataSource(dataSource, Some(19))
}

trait HasDatabaseConfig[P <: BasicProfile] {

  protected val component: DbComponent[P]

  protected final lazy val profile: P = component.driver

  protected final def db: P#Backend#Database = component.db
}
