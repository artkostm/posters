package com.artkostm.posters

import cats.MonadError
import ciris._

abstract class Configuration[F[_], Conf](implicit me: MonadError[F, Throwable]) {
  def load: F[Conf] = config match {
    case Right(c) => me.pure(c)
    case Left(errors) => me.raiseError(errors.toException)
  }

  protected def config: Either[ConfigErrors, Conf]
}
