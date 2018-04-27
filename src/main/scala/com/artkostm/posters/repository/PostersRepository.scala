package com.artkostm.posters.repository

import javax.sql.DataSource

import com.google.inject.Inject

import scala.concurrent.ExecutionContext

trait PostgresPostersRepository extends AssignRepository
                                    with DaysRepository
                                    with InfoRepository
                                    with TestRepo
                                    with JsonSupportDbComponent {
  import driver.api._

  def setUp()(implicit ec: ExecutionContext) =
    db.run((setUpAssign >> setUpDays >> setUpInfo >> sqlu"""
            DELETE FROM info WHERE NOT EXISTS (SELECT * FROM days WHERE categories::jsonb::text LIKE '%' || info.link || '%')
          """).transactionally)

  def close() = db.close()
}

class PostersRepository @Inject() (ds: DataSource) extends PostgresPostersRepository {
  override protected def dataSource: DataSource = ds
}
