package com.artkostm.posters.worker.migration

import java.sql.Connection

import cats.effect._
import com.artkostm.posters.Configuration.DatabaseConfig
import com.artkostm.posters.worker.config.AppConfig
import com.artkostm.posters.worker.migration.v1.V0001__CreateVisitors
import doobie._
import doobie.hikari.HikariTransactor
import doobie.implicits._
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.migration.jdbc.JdbcMigration

import scala.concurrent.ExecutionContext

trait DoobieMigration extends JdbcMigration {
  def migrate: ConnectionIO[_]

  implicit val cs = IO.contextShift(ExecutionContext.global)

  override def migrate(connection: Connection): Unit = {
    ExecutionContexts.cachedThreadPool[IO].use { implicit ec =>
      val xa = Transactor.fromConnection[IO](connection, ec)
      migrate.transact(xa)
    }
  }
}

object DoobieMigration {

  def run[F[_]](config: AppConfig)(implicit F: Sync[F]): F[Int] = F.delay {
    val flyway   = new Flyway
    val location = classOf[V0001__CreateVisitors].getPackage.getName.replace(".", "/")

    flyway.setDataSource(config.db.url.value, config.db.user, config.db.password)
    flyway.setLocations(location)
    flyway.migrate()
  }

  def transactor[F[_]: Async: ContextShift](dbConfig: DatabaseConfig): Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32)
      te <- ExecutionContexts.cachedThreadPool[F]
      xa <- HikariTransactor.newHikariTransactor[F](driverClassName = dbConfig.driver.value,
                                                    url = dbConfig.url.value,
                                                    user = dbConfig.user,
                                                    pass = dbConfig.password,
                                                    connectEC = ce,
                                                    transactEC = te)
    } yield xa
}
