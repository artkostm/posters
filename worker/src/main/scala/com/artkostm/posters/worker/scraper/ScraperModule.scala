package com.artkostm.posters.worker.scraper

import cats.effect.{Async, IO}
import com.artkostm.posters.worker.config.{AppConfig, AppConfiguration}
import fs2.StreamApp
import fs2.StreamApp.ExitCode

class ScraperModule[F[_]: Async](appConfig: AppConfig) {
  val eventScraper = new EventInfoScraper[F](appConfig.scraper)
}

object testS extends StreamApp[IO] {
  override def stream (args: List[String], requestShutdown: IO[Unit] ): fs2.Stream[IO, StreamApp.ExitCode] = {
    fs2.Stream.eval(AppConfiguration.load).flatMap (appConfig => {
      println (appConfig)
      fs2.Stream.eval(new EventInfoScraper[IO](appConfig.scraper).event("https://afisha.tut.by/film/venom/"))
    }).flatMap(info => {
      println(info)
      fs2.Stream.eval(IO.pure (ExitCode.Success))
    })
  }
}