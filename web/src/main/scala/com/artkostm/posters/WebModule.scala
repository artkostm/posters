package com.artkostm.posters

import cats.effect._
import com.artkostm.posters.config.{AppConfig, WebConfiguration}
import com.artkostm.posters.endpoint.{CategoryEndpoint, InfoEndpoint, VisitorEndpoint}
import com.artkostm.posters.interpreter.{EventStoreInterpreter, InfoStoreInterpreter, VisitorStoreInterpreter, VisitorValidationInterpreter}
import com.artkostm.posters.scraper.{AfishaScraper, Scraper}
import doobie.hikari.HikariTransactor
import cats.syntax.semigroupk._
import cats.syntax.functor._
import com.olegpy.meow.hierarchy._
import com.artkostm.posters.Configuration.DatabaseConfig
import com.artkostm.posters.endpoint.error.HttpErrorHandler
import com.artkostm.posters.service.VisitorsService

class WebModule[F[_]: Effect](val config: AppConfig, val xa: HikariTransactor[F]) {

  private lazy val infoStore    = new InfoStoreInterpreter(xa.trans)
  private lazy val eventStore   = new EventStoreInterpreter(xa.trans)
  private lazy val visitorStore = new VisitorStoreInterpreter(xa.trans)

  private lazy val visitorValidator = new VisitorValidationInterpreter[F](visitorStore)
  private lazy val visitorService   = new VisitorsService[F](visitorStore, visitorValidator)
  private implicit val errorHandler = new HttpErrorHandler[F]

  lazy val scraper: Scraper[F] = new AfishaScraper[F](config.scraper)

  lazy val endpoints = InfoEndpoint[F](infoStore) <+> CategoryEndpoint[F](eventStore, scraper) <+> VisitorEndpoint[F](
    visitorService)
}

object WebModule {
  def init[F[_]: ContextShift: Effect]: Resource[F, WebModule[F]] =
    for {
      config <- Resource.liftF(WebConfiguration.load)
      xa     <- DatabaseConfig.transactor(config.db)
    } yield new WebModule[F](config, xa)
}
