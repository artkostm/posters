package com.artkostm.posters.worker

import cats.implicits._
import cats.effect.{ContextShift, Effect, Resource, Sync}
import com.artkostm.posters.interpreter.{EventStoreInterpreter, InfoStoreInterpreter, VisitorStoreInterpreter}
import com.artkostm.posters.worker.config.{AppConfig, AppConfiguration}
import com.artkostm.posters.worker.migration.DoobieMigration
import com.artkostm.posters.worker.scraper.EventScraper
import doobie.hikari.HikariTransactor

class WorkerModule[F[_]: Effect](config: AppConfig, val xa: HikariTransactor[F]) {
  lazy val scraper      = new EventScraper[F](config.scraper)
  private lazy val infoStore    = new InfoStoreInterpreter()
  private lazy val eventStore   = new EventStoreInterpreter()
  private lazy val visitorStore = new VisitorStoreInterpreter()
}

object WorkerModule {
  def init[F[_]: Effect: ContextShift](implicit F: Sync[F]): F[WorkerModule[F]] =
    for {
      config <- AppConfiguration.load[F]
      _      <- DoobieMigration.run[F](config)
      module <- DoobieMigration.transactor(config.db).use(xa => F.delay(new WorkerModule[F](config, xa)))
    } yield module
}
