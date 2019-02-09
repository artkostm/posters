package com.artkostm.posters

import cats.MonadError
import ciris._
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.auto._

trait Configuration[Conf] {
  def load[F[_]](implicit me: MonadError[F, Throwable]): F[Conf] = config match {
    case Right(c)     => me.pure(c)
    case Left(errors) => me.raiseError(errors.toException)
  }

  protected def config: Either[ConfigErrors, Conf]
}

object Configuration {
  val AppVersion = "3.0.0"

  val buildDbConfig: String => DatabaseConfig =
    DatabaseConfig(_,
                   driver = "org.postgresql.Driver",
                   user = "test",
                   password = "12345",
                   numThreads = 10,
                   maxConnections = 15,
                   minConnections = 4)

  val buildDbConfigForHeroku: (String, NonEmptyString, NonEmptyString) => DatabaseConfig =
    (url, user, password) =>
      DatabaseConfig(url,
                     driver = "org.postgresql.Driver",
                     user = user,
                     password = password,
                     numThreads = 10,
                     maxConnections = 15,
                     minConnections = 4)

  final case class DatabaseConfig(
      url: String,
      driver: NonEmptyString,
      user: String = "",
      password: String = "",
      numThreads: PosInt,
      maxConnections: PosInt,
      minConnections: PosInt
  )
}
