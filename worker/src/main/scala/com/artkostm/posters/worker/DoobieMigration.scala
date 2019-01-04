package com.artkostm.posters.worker

import java.sql.Connection

import cats.effect.{IO, Sync}
import com.artkostm.posters.worker.config.AppConfig
import doobie._
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
    val location = classOf[migration.V0001__CreateVisitors].getPackage.getName.replace(".", "/")

    flyway.setDataSource(config.db.url.value, config.db.user, config.db.password)
    flyway.setLocations(location)
    flyway.migrate()
  }
}
