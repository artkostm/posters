package com.artkostm.posters.worker

import cats.effect.Async
import com.artkostm.posters.worker.config.AppConfig
import fs2.Stream

final case class Context[F[_]](config: AppConfig)

object Context {
  def prepare[F[_]: Async](config: AppConfig): Stream[F, Context[F]] =
    for {
      _ <- Stream.eval(DoobieMigration.run[F](config))
    } yield Context[F](config)
}
