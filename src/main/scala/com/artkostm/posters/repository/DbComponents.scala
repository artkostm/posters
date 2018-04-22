package com.artkostm.posters.repository

import javax.sql.DataSource

import com.google.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

trait H2DbComponent extends DbComponent {
  override lazy val driver = slick.jdbc.H2Profile

  import driver.api._

  override lazy val db: driver.api.Database = Database.forConfig("h2mem1")
}

trait JsonSupportDbComponent extends DbComponent {
  protected def dataSource: DataSource
  override lazy val driver = JsonSupportPostgresProfile

  import driver.api._

  override lazy val db: driver.api.Database = Database.forDataSource(dataSource, Some(19))
}

trait PostgresPostersRepository extends AssignRepository
                                    with DaysRepository
                                    with InfoRepository
                                    with JsonSupportDbComponent {
  import driver.api._
  def setUp()(implicit ec: ExecutionContext) =
    db.run((setUpAssign >> setUpDays >> setUpInfo >> sqlu"""
            DELETE FROM info WHERE NOT EXISTS (SELECT * FROM days WHERE categories::jsonb::text LIKE '%' || info.link || '%')
          """).transactionally)
}

class PostersRepository @Inject() (ds: DataSource) extends PostgresPostersRepository {
  override protected def dataSource: DataSource = ds
}