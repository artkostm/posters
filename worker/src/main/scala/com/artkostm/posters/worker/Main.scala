package com.artkostm.posters.worker

import cats.effect._
import cats.implicits._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    WorkerModule.init[IO].use(_.collector.collect().compile.drain.as(ExitCode.Success))
}
