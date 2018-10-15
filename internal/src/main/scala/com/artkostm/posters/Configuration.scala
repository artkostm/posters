package com.artkostm.posters

import cats.MonadError
import ciris._
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.auto._

abstract class Configuration[F[_], Conf](implicit me: MonadError[F, Throwable]) {
  def load: F[Conf] = config match {
    case Right(c) => me.pure(c)
    case Left(errors) => me.raiseError(errors.toException)
  }

  protected def config: Either[ConfigErrors, Conf]
}

object Configuration {
  val APP_VERSION = "2.6.0"
  val NUM_THREADS = 10
  val MAX_CONNECTIONS = 15
  val MIN_CONNECTIONS = 4

  val buildDbConfig: NonEmptyString => DatabaseConfig = DatabaseConfig(
    _,
    driver = "org.postgresql.Driver",
    numThreads = NUM_THREADS,
    maxConnections = MAX_CONNECTIONS,
    minConnections = MIN_CONNECTIONS)

  val buildDbConfigForHeroku: (NonEmptyString, NonEmptyString, NonEmptyString) => DatabaseConfig =
    (url, user, password) => DatabaseConfig(
      url,
      driver = "org.postgresql.Driver",
      user = user,
      password = password,
      numThreads = NUM_THREADS,
      maxConnections = MAX_CONNECTIONS,
      minConnections = MIN_CONNECTIONS)

  final case class DatabaseConfig(
                                   url: NonEmptyString,
                                   driver: NonEmptyString,
                                   user: NonEmptyString = NonEmptyString(""),
                                   password: NonEmptyString = NonEmptyString(""),
                                   numThreads: PosInt,
                                   maxConnections: PosInt,
                                   minConnections: PosInt
                                 )
}