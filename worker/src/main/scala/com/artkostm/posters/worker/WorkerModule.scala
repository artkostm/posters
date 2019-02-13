package com.artkostm.posters.worker

import cats.effect._
import com.artkostm.posters.Configuration.DatabaseConfig
import com.artkostm.posters.interpreter.{EventStoreInterpreter, InfoStoreInterpreter, VisitorStoreInterpreter}
import com.artkostm.posters.worker.collector.EventCollector
import com.artkostm.posters.worker.config.{AppConfig, AppConfiguration}
import com.artkostm.posters.worker.migration.DoobieMigration
import com.artkostm.posters.worker.scraper.AfishaScraper
import doobie.hikari.HikariTransactor

class WorkerModule[F[_]: Timer: Concurrent](config: AppConfig, val xa: HikariTransactor[F]) {
  private lazy val scraper      = new AfishaScraper[F](config.scraper)
  private lazy val infoStore    = new InfoStoreInterpreter(xa.trans)
  private lazy val eventStore   = new EventStoreInterpreter(xa.trans)
  private lazy val visitorStore = new VisitorStoreInterpreter(xa.trans)
  lazy val collector            = new EventCollector[F](scraper, eventStore, infoStore, visitorStore)
}

object WorkerModule {
  def init[F[_]: ContextShift: Timer: Concurrent](): Resource[F, WorkerModule[F]] =
    for {
      config <- Resource.liftF(AppConfiguration.load[F])
      _      <- Resource.liftF(DoobieMigration.run[F](config))
      xa     <- DatabaseConfig.transactor(config.db)
    } yield new WorkerModule[F](config, xa)
}
