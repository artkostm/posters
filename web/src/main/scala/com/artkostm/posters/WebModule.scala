package com.artkostm.posters

import cats.effect.{Effect, Resource}
import com.artkostm.posters.config.AppConfig
import com.artkostm.posters.scraper.{AfishaScraper, Scraper}
import org.http4s.server.Server

class WebModule[F[_]: Effect](config: AppConfig) {
  lazy val scraper: Scraper[F] = new AfishaScraper[F](config.scraper)
}

object WebModule {
//  def init[F[_]]: Resource[F, Server[F]] = for {
//
//  }
}
