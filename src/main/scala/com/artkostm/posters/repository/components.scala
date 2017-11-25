package com.artkostm.posters.repository

import com.artkostm.posters._

trait PostgresDbComponent extends DbComponent {
  override lazy val driver = slick.jdbc.PostgresProfile

  import driver.api._

  override lazy val db: driver.api.Database = Database.forDataSource(postgresDS, Some(19))
}

object PostgresEventsRepository extends EventsRepository with PostgresDbComponent