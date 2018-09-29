package com.artkostm.posters.worker

import cats.effect.Async
import fs2.Stream

final case class Context[F[_]]()

object Context {
  def prepare[F[_]: Async]: Stream[F, Context[F]] =
    for {
      _ <- Stream.eval(DoobieMigration.run[F]())
    } yield Context[F]()
}
