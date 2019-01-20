package com.artkostm.posters.worker

import cats.implicits._
import cats.effect.Effect
import com.artkostm.posters.worker.config.{AppConfig, AppConfiguration}
import com.artkostm.posters.worker.migration.DoobieMigration
import doobie.hikari.HikariTransactor

class WorkerModule[F[_]: Effect](config: AppConfig, val xa: HikariTransactor[F]) {}

object WorkerModule {
  def init[F[_]: Effect](): F[WorkerModule[F]] =
    for {
      config <- AppConfiguration.load[F]
      _      <- DoobieMigration.run[F](config)
      xa     <- DoobieMigration.transactor(config.db)
    } yield new WorkerModule[F](config, xa)
}
