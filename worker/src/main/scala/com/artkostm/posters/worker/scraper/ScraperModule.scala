package com.artkostm.posters.worker.scraper

import cats.effect.{Async, IO}
import com.artkostm.posters.worker.config.{AppConfig, AppConfiguration}
import fs2.StreamApp
import fs2.StreamApp.ExitCode
import org.joda.time.DateTime

class ScraperModule[F[_]: Async](appConfig: AppConfig) {
  val eventScraper = new EventScraper[F](appConfig.scraper)
}

object testS extends StreamApp[IO] {
  override def stream (args: List[String], requestShutdown: IO[Unit] ): fs2.Stream[IO, StreamApp.ExitCode] = {
    val stream = for {
      config <- fs2.Stream.eval(AppConfiguration.load)
      scraper <- fs2.Stream.eval(IO.pure(new EventScraper[IO](config.scraper)))
      day <- fs2.Stream.range(1, 31).covary[IO]
      event <- fs2.Stream.eval(scraper.event(DateTime.now().plusDays(day)))
    } yield {
      println(event)
    }

    fs2.Stream.eval(stream.compile.drain.attempt.map(_.fold(_ => 1, _ => 0)).map(ExitCode.fromInt(_)))
  }
}
