package com.artkostm.posters.worker

import cats.effect.{Effect, IO}
import com.artkostm.posters.worker.config.{AppConfig, AppConfiguration}
import doobie.hikari.HikariTransactor

class WorkerModule[F[_]: Effect](config: AppConfig, val xa: HikariTransactor[F]) {}

object WorkerModule {
  def init[F[_]: Effect]: F[WorkerModule[F]] =
    for {
      config <- AppConfiguration.load
      xa <- HikariTransactor.newHikariTransactor[F](driverClassName = "org.postgresql.Driver",
                                                    url = "jdbc:postgresql://localhost:5432/postgres",
                                                    user = "test",
                                                    pass = "12345")
    } yield new WorkerModule[F](config, xa)
}
