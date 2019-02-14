package com.artkostm.posters.worker.config

import java.net.URI

import ciris._
import ciris.refined._
import ciris.enumeratum._
import com.artkostm.posters.Configuration
import com.artkostm.posters.Configuration.{DatabaseConfig, ScraperConfig, TutScraper}
import com.artkostm.posters.environments.AppEnvironment
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString

object AppConfiguration extends Configuration[AppConfig] {

  import com.artkostm.posters.environments.AppEnvironment._

  override protected def config =
    withValue(env[AppEnvironment]("APP_ENV").orElse(ConfigValue(Right(Local)))) {
      case Local =>
        loadConfig(env[Option[String]]("DOCKER_HOST")) { dockerHost =>
          AppConfig(
            version = Configuration.AppVersion,
            scraper = scraperConfig,
            db = Configuration.buildDbConfig(
              s"jdbc:postgresql://${dockerHost.map(URI.create).map(_.getHost).getOrElse("localhost")}:5432/postgres")
          )
        }
      case Production | Heroku =>
        loadConfig(env[NonEmptyString]("JDBC_DATABASE_URL"),
                   env[NonEmptyString]("JDBC_DATABASE_USERNAME"),
                   env[NonEmptyString]("JDBC_DATABASE_PASSWORD")) { (dbUrl, user, password) =>
          AppConfig(version = Configuration.AppVersion,
                    scraper = scraperConfig,
                    db = Configuration.buildDbConfigForHeroku(dbUrl, user, password))
        }
    }.result
}

case class AppConfig(version: String, scraper: ScraperConfig, db: DatabaseConfig)
