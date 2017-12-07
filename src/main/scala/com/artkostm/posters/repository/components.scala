package com.artkostm.posters.repository

import com.artkostm.posters._

import scala.concurrent.{ExecutionContext, Future}

trait H2DbComponent extends DbComponent {
  override lazy val driver = slick.jdbc.H2Profile

  import driver.api._

  override lazy val db: driver.api.Database = Database.forConfig("h2mem1")
}

trait JsonSupportDbComponent extends DbComponent {
  override lazy val driver = JsonSupportPostgresProfile

  import driver.api._

  override lazy val db: driver.api.Database = Database.forDataSource(postgresDS, Some(19))
}

object PostgresPostersRepository extends AssignRepository
                                    with DaysRepository
                                    with InfoRepository
                                    with JsonSupportDbComponent {
  import driver.api._
  def setUp()(implicit ec: ExecutionContext): Future[List[Any]] = Future.sequence(List(setUpAssign(), setUpDays(), setUpInfo(),
    db.run(
      sqlu"""
            DELETE FROM info WHERE NOT EXISTS (SELECT * FROM days WHERE categories::jsonb::text LIKE '%' || info.link || '%')
          """)))
}

object H2AssignRepository extends AssignRepository with H2DbComponent
