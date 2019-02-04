package com.artkostm.posters.worker

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
