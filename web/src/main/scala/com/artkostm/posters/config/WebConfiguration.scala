package com.artkostm.posters.config

import java.net.URI

import ciris._
import ciris.refined._
import ciris.enumeratum._
import com.artkostm.posters.Configuration
import com.artkostm.posters.Configuration.{DatabaseConfig, ScraperConfig}
import com.artkostm.posters.config.WebConfiguration.ApiKey
import com.artkostm.posters.environments.AppEnvironment
import com.artkostm.posters.environments.AppEnvironment._
import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.string.NonEmptyString

object WebConfiguration extends Configuration[AppConfig] {
  type ApiKey = String Refined MatchesRegex[W.`"[a-zA-Z0-9]{25,40}"`.T]

  override protected def config =
    withValue(env[AppEnvironment]("APP_ENV").orElse(ConfigValue(Right(Local)))) {
      case Local =>
        loadConfig(env[Option[String]]("DOCKER_HOST")) { dockerHost =>
          AppConfig(
            version = Configuration.AppVersion,
            http = HttpConfig(8080),
            db = Configuration.buildDbConfig(
              s"jdbc:postgresql://${dockerHost.map(URI.create).map(_.getHost).getOrElse("localhost")}:5432/postgres"),
            api = ApiConfig(Secret("uufdeddddd00d0d00d0d00d0d0"), Secret("123token456")),
            scraperConfig
          )
        }
      case Production | Heroku =>
        loadConfig(
          env[Secret[ApiKey]]("API_KEY").orElse(prop("api.key")),
          env[Secret[String]]("API_TOKEN"),
          env[UserPortNumber]("PORT"),
          env[NonEmptyString]("JDBC_DATABASE_URL"),
          env[NonEmptyString]("JDBC_DATABASE_USERNAME"),
          env[NonEmptyString]("JDBC_DATABASE_PASSWORD"),
        ) { (apiKey, apiToken, port, dbUrl, user, password) =>
          AppConfig(
            version = Configuration.AppVersion,
            http = HttpConfig(port),
            db = Configuration.buildDbConfigForHeroku(dbUrl, user, password),
            api = ApiConfig(apiKey, apiToken),
            scraperConfig
          )
        }
    }.result
}

case class HttpConfig(port: UserPortNumber)
case class ApiConfig(key: Secret[ApiKey], token: Secret[String])
case class AppConfig(version: String, http: HttpConfig, db: DatabaseConfig, api: ApiConfig, scraper: ScraperConfig)
