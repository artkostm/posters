package com.artkostm.posters.fp.config

import cats.effect.IO
import ciris._
import ciris.refined._
import ciris.enumeratum._
import com.artkostm.posters.Configuration
import com.artkostm.posters.Configuration.DatabaseConfig
import com.artkostm.posters.environments.AppEnvironment
import com.artkostm.posters.fp.config.WebConfiguration.ApiKey
import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.string.NonEmptyString

object WebConfiguration extends Configuration[IO, AppConfig] {
  type ApiKey = String Refined MatchesRegex[W.`"[a-zA-Z0-9]{25,40}"`.T]

  import com.artkostm.posters.environments.AppEnvironment._

  override protected def config =
    withValue(env[AppEnvironment]("APP_ENV").orElse(ConfigValue(Right(Local)))) {
      case Local => loadConfig {
        AppConfig(version = Configuration.APP_VERSION,
          http = HttpConfig(8080),
          db = Configuration.buildDbConfig("url"),
          api = ApiConfig(Secret("uufdeddddd00d0d00d0d00d0d0"), "token"))
      }
      case Production | Heroku => loadConfig(
        env[Secret[ApiKey]]("API_KEY").orElse(prop("api.key")),
        env[UserPortNumber]("PORT"),
        env[NonEmptyString]("JDBC_DATABASE_URL"),
        env[NonEmptyString]("JDBC_DATABASE_USERNAME"),
        env[NonEmptyString]("JDBC_DATABASE_PASSWORD")) { (apiKey, port, dbUrl, user, password) =>
          AppConfig(version = Configuration.APP_VERSION,
            http = HttpConfig(port),
            db = Configuration.buildDbConfigForHeroku(dbUrl, user, password),
            api = ApiConfig(apiKey, "token"))
      }
  }
}

case class HttpConfig(port: UserPortNumber)
case class ApiConfig(key: Secret[ApiKey], token: String)
case class AppConfig(version: String, http: HttpConfig, db: DatabaseConfig, api: ApiConfig)
