package com.artkostm.posters.worker.config

import java.net.URI

import cats.effect.Sync
import ciris._
import ciris.cats.effect._
import ciris.refined._
import ciris.enumeratum._
import com.artkostm.posters.Configuration
import com.artkostm.posters.Configuration.{DatabaseConfig, ScraperConfig}
import com.artkostm.posters.environments.AppEnvironment
import com.artkostm.posters.environments.AppEnvironment._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString

private[config] class AppConfiguration[F[_]](implicit F: Sync[F]) extends Configuration[F, AppConfig] {

  override protected def config =
    withValue(
      envF[F, AppEnvironment]("APP_ENV").orElse(ConfigValue.applyF[F, AppEnvironment](F.pure(Right(Local))))
    ) {
      case Local =>
        loadConfig(envF[F, Option[String]]("DOCKER_HOST")) { dockerHost =>
          AppConfig(
            version = Configuration.AppVersion,
            scraper = scraperConfig,
            db = Configuration.buildDbConfig(
              s"jdbc:postgresql://${dockerHost.map(URI.create).map(_.getHost).getOrElse("localhost")}:5432/postgres")
          )
        }
      case Production | Heroku =>
        loadConfig(envF[F, NonEmptyString]("JDBC_DATABASE_URL"),
                   envF[F, NonEmptyString]("JDBC_DATABASE_USERNAME"),
                   envF[F, NonEmptyString]("JDBC_DATABASE_PASSWORD")) { (dbUrl, user, password) =>
          AppConfig(version = Configuration.AppVersion,
                    scraper = scraperConfig,
                    db = Configuration.buildDbConfigForHeroku(dbUrl, user, password))
        }
    }.result
}

object AppConfiguration {
  def load[F[_]: Sync]() = new AppConfiguration[F]().load
}

case class AppConfig(version: String, scraper: ScraperConfig, db: DatabaseConfig)
