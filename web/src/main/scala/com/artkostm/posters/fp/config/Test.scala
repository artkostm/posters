package com.artkostm.posters.fp.config

import cats.effect.IO
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode

object Test extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    fs2.Stream.eval(WebConfiguration.load).flatMap(appConfig => {
      println(appConfig)
      Stream.eval(IO.pure(ExitCode.Success))
    })
  }
}
