package com.artkostm.posters.repository

import javax.sql.DataSource
import slick.basic.BasicProfile

sealed trait DbComponent[P <: BasicProfile] {
  protected[repository] val driver: P

  protected[repository] val database: P#Backend#Database
}

trait JsonSupportDbComponent extends DbComponent[PostersPgProfile] with HasDatabaseConfig[PostersPgProfile] {
  protected override val component = this
  protected def dataSource: DataSource
  protected override lazy val driver = PostersPgProfile

  import driver.api._
  override lazy val database = Database.forDataSource(dataSource, Some(19))
}

trait HasDatabaseConfig[P <: BasicProfile] {

  protected val component: DbComponent[P]

  protected final lazy val profile: P = component.driver

  protected final def db: P#Backend#Database = component.database
}
