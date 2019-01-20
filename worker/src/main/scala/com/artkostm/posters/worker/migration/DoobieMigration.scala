package com.artkostm.posters.worker.migration

import java.sql.Connection

import cats.effect.{Async, IO, Sync}
import com.artkostm.posters.Configuration.DatabaseConfig
import com.artkostm.posters.worker.config.AppConfig
import com.artkostm.posters.worker.migration.v1.V0001__CreateVisitors
import doobie._
import doobie.hikari.HikariTransactor
import doobie.implicits._
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.migration.jdbc.JdbcMigration

trait DoobieMigration extends JdbcMigration {
  def migrate: ConnectionIO[_]

  override def migrate(connection: Connection): Unit = {
    val xa = Transactor.fromConnection[IO](connection)
    migrate.transact(xa).unsafeRunSync()
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

  def transactor[F[_]: Async](dbConfig: DatabaseConfig): F[HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](driverClassName = dbConfig.driver.value,
                                            url = dbConfig.url.value,
                                            user = dbConfig.user,
                                            pass = dbConfig.password)
}
