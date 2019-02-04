package com.artkostm.posters.worker

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}
import java.time.temporal.ChronoUnit
import java.util.Locale

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- WorkerModule.init[IO]
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