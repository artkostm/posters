package com.artkostm.posters.worker

import cats.effect.IO
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}

object Main extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] =
    Context.prepare[IO].flatMap(_ => Stream.eval(IO.pure(ExitCode.Success)))
}
