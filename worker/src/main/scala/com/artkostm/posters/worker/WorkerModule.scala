package com.artkostm.posters.worker

import cats.implicits._
import cats.effect.{ContextShift, Effect, Sync}
import com.artkostm.posters.interpreter.{EventStoreInterpreter, InfoStoreInterpreter}
import com.artkostm.posters.worker.config.{AppConfig, AppConfiguration}
import com.artkostm.posters.worker.migration.DoobieMigration
import doobie.hikari.HikariTransactor

class WorkerModule[F[_]: Effect](config: AppConfig, val xa: HikariTransactor[F]) {
  private lazy val infoStore  = new InfoStoreInterpreter()
  private lazy val eventStore = new EventStoreInterpreter()
}

object WorkerModule {
  def init[F[_]: Effect: ContextShift](implicit F: Sync[F]): F[WorkerModule[F]] =
    for {
      config <- AppConfiguration.load[F]
      _      <- DoobieMigration.run[F](config)
      xa     <- DoobieMigration.transactor(config.db).use(xa => F.delay(new WorkerModule[F](config, xa)))
    } yield xa
}
