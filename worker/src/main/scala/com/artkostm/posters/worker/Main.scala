package com.artkostm.posters.worker

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}
import java.time.temporal.ChronoUnit
import java.util.Locale

import cats.effect.IO
import com.artkostm.posters.worker.config.AppConfiguration
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}

object Main extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] =
    for {
      _ <- Stream.eval(WorkerModule.init[IO])
    } yield ExitCode.Success
}

object InstantTest extends App {
  val dates = (-2 to 30).map(Instant.now().truncatedTo(ChronoUnit.DAYS).plus(_, ChronoUnit.DAYS))
  val format = DateTimeFormatter
    .ofPattern("yyyy/MM/dd")
    .withLocale(Locale.ENGLISH)
    .withZone(ZoneId.of("Europe/Minsk"))

  dates.map(format.format).foreach(println)
}