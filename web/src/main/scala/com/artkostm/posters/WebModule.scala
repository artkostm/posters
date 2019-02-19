package com.artkostm.posters

import cats.effect._
import com.artkostm.posters.config.{AppConfig, WebConfiguration}
import com.artkostm.posters.endpoint.{CategoryEndpoint, InfoEndpoint}
import com.artkostm.posters.interpreter.{EventStoreInterpreter, InfoStoreInterpreter, VisitorStoreInterpreter}
import com.artkostm.posters.scraper.{AfishaScraper, Scraper}
import doobie.hikari.HikariTransactor
import cats.syntax.semigroupk._
import com.artkostm.posters.Configuration.DatabaseConfig

class WebModule[F[_]: Effect](val config: AppConfig, val xa: HikariTransactor[F]) {
  private lazy val infoStore    = new InfoStoreInterpreter(xa.trans)
  private lazy val eventStore   = new EventStoreInterpreter(xa.trans)
  private lazy val visitorStore = new VisitorStoreInterpreter(xa.trans)
  lazy val endpoints            = InfoEndpoint[F](infoStore) <+> CategoryEndpoint[F](eventStore)
  lazy val scraper: Scraper[F]  = new AfishaScraper[F](config.scraper)
}

object WebModule {
  def init[F[_]: ContextShift: Effect]: Resource[F, WebModule[F]] =
    for {
      config <- Resource.liftF(WebConfiguration.load)
      xa     <- DatabaseConfig.transactor(config.db)
    } yield new WebModule[F](config, xa)
}