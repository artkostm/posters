package com.artkostm.posters.worker

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import cats.effect._
import cats.implicits._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    WorkerModule
      .init[IO]
      .use(
        _.collector
          .collect((-2 to 31).map(LocalDate.now().plus(_, ChronoUnit.DAYS)))
          .compile
          .drain
          .as(ExitCode.Success))
}
