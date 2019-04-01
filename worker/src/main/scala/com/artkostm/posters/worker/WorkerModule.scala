package com.artkostm.posters.worker

import cats.effect._
import com.artkostm.posters.Configuration.DatabaseConfig
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.interpreter.{EventStoreInterpreter, InfoStoreInterpreter, VisitorStoreInterpreter}
import com.artkostm.posters.scraper.{AfishaScraper, Scraper}
import com.artkostm.posters.worker.collector.EventCollector
import com.artkostm.posters.worker.config.{AppConfig, AppConfiguration}
import com.artkostm.posters.worker.migration.DoobieMigration
import doobie.hikari.HikariTransactor

class WorkerModule[F[_]: Timer: Concurrent](config: AppConfig, val xa: HikariTransactor[F]) {
  private lazy val scraper      = new AfishaScraper[F](config.scraper)
   lazy val infoStore    = new InfoStoreInterpreter(xa.trans)
  lazy val eventStore   = new EventStoreInterpreter(xa.trans)
  private lazy val visitorStore = new VisitorStoreInterpreter(xa.trans)
  lazy val collector            = new EventCollector[F](scraper, eventStore, infoStore, visitorStore)
}

object WorkerModule {
  def init[F[_]: ContextShift: Timer: Concurrent](): Resource[F, WorkerModule[F]] =
    for {
      config <- Resource.liftF(AppConfiguration.load[F])
      _      <- Resource.liftF(DoobieMigration.run[F](config))
      xa     <- DatabaseConfig.transactor[F](config.db): Resource[F, HikariTransactor[F]]
    } yield new WorkerModule[F](config, xa)
}

//class ISI[F[_]: Sync] extends InfoStore[F] {
//  override def deleteOld(): F[Int] = Sync[F].delay {
//    println("deleting old event info")
//    0
//  }
//  override def save(info: EventInfo): F[EventInfo] = Sync[F].delay {
//    println(s"saving event info $info")
//    info
//  }
//  override def find(link: String): F[Option[EventInfo]] = Sync[F].delay {
//    println(s"searching event info using $link")
//    None
//  }
//}
