package com.artkostm.posters.repository

import com.artkostm.posters._

trait H2DbComponent extends DbComponent {
  override val driver = slick.jdbc.H2Profile

  import driver.api._

  override val db: driver.api.Database = Database.forConfig("h2mem1")
}

trait PostgresDbComponent extends DbComponent {
  override val driver = slick.jdbc.PostgresProfile

  import driver.api._

  override val db: driver.api.Database = Database.forDataSource(postgresDS, Some(19))
}

object PostgresEventsRepository extends EventsRepository with PostgresDbComponent

object H2EventsRepository extends EventsRepository with H2DbComponent