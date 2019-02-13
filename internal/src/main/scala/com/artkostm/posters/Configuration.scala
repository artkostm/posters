package com.artkostm.posters

import cats.effect._
import cats.MonadError
import ciris._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
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
                   password = "12345")

  val buildDbConfigForHeroku: (String, NonEmptyString, NonEmptyString) => DatabaseConfig =
    (url, user, password) =>
      DatabaseConfig(url,
                     driver = "org.postgresql.Driver",
                     user = user,
                     password = password)

  final case class DatabaseConfig(
      url: String,
      driver: NonEmptyString,
      user: String = "",
      password: String = "",
      numThreads: PosInt = 32,
      maxConnections: PosInt = 15,
      minConnections: PosInt = 14
  )

  object DatabaseConfig {
    def transactor[F[_]: Async: ContextShift](dbConfig: DatabaseConfig): Resource[F, HikariTransactor[F]] =
      for {
        ce <- ExecutionContexts.fixedThreadPool[F](dbConfig.numThreads)
        te <- ExecutionContexts.cachedThreadPool[F]
        xa <- HikariTransactor.newHikariTransactor[F](driverClassName = dbConfig.driver.value,
                                                      url = dbConfig.url,
                                                      user = dbConfig.user,
                                                      pass = dbConfig.password,
                                                      connectEC = ce,
                                                      transactEC = te)
      } yield xa
  }
}
