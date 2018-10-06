package com.artkostm.posters.fp.config

import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import ciris._
import ciris.refined._
import ciris.enumeratum._
import com.artkostm.posters.environments.AppEnvironment
import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

object Configuration {
  type ApiKey = String Refined MatchesRegex[W.`"[a-zA-Z0-9]{25,40}"`.T]

  private val buildDbConfig: NonEmptyString => DatabaseConfig = DatabaseConfig(
    _,
    driver = "org.postgresql.Driver",
    numThreads = 10,
    maxConnections = 19,
    minConnections = 4)

  private val scraperConfig = ScraperConfig(TutScraper(
      url = "https://afisha.tut.by/day/",
      format = DateTimeFormat.forPattern("yyyy/MM/dd"),
      blocksSelector = "#events-block .events-block",
      blockTitleSelector = ".title_block",
      eventsSelector = ".lists__li",
      mediaSelector = "a.media",
      eventNameSelector = "a.name span",
      descriptionSelector = "div.txt",
      descriptionTextSelector = "p",
      ticketSelector = "a.ticket",
      freeEventSelector = "a.free-event",
      hrefAttrSelector = "href",
      srcAttrSelector = "src",
      imgSelector = "img",
      eventPhotoSelector = "#event-photos a",
      eventDescriptionSelector = "#event-description",
      commentsSelector = "#comments .comments__content",
      commentAuthorSelector = ".head span.author",
      commentDateSelector = ".head .date",
      commentTextSelector = ".comment_txt"
  ))

  import com.artkostm.posters.environments.AppEnvironment._
  lazy val config = withValue(env[AppEnvironment]("APP_ENV").orElse(ConfigValue(Right(Local)))) {
    case Local => loadConfig {
      AppConfig(version = "2.6.0",
        scraperConfig = scraperConfig,
        httpConfig = HttpConfig(8080),
        databaseConfig = buildDbConfig("url"),
        apiConfig = ApiConfig(Secret("uufdeddddd00d0d00d0d00d0d0"), "token"))
    }
    case Production => loadConfig(
      env[Secret[ApiKey]]("API_KEY").orElse(prop("api.key")),
      env[UserPortNumber]("PORT"), env[NonEmptyString]("DATABASE_URL")) { (apiKey, port, dbUrl) =>
        AppConfig(version = "2.6.0",
          scraperConfig = scraperConfig,
          httpConfig = HttpConfig(port),
          databaseConfig = buildDbConfig(dbUrl),
          apiConfig = ApiConfig(apiKey, "token"))
    }
  }

  case class TutScraper(url: String, format: DateTimeFormatter, blocksSelector: String,
                        blockTitleSelector: String, eventsSelector: String,
                        mediaSelector: String, eventNameSelector: String,
                        descriptionSelector: String, descriptionTextSelector: String,
                        ticketSelector: String, freeEventSelector: String,
                        hrefAttrSelector: String, srcAttrSelector: String,
                        imgSelector: String, eventPhotoSelector: String,
                        eventDescriptionSelector: String, commentsSelector: String,
                        commentAuthorSelector: String, commentDateSelector: String,
                        commentTextSelector: String)
  case class ScraperConfig(tut: TutScraper)
  case class HttpConfig(port: UserPortNumber)
  case class DatabaseConfig(url: NonEmptyString, driver: NonEmptyString, numThreads: PosInt, maxConnections: PosInt, minConnections: PosInt)
  case class ApiConfig(apiKey: Secret[ApiKey], apiToken: String)
  case class AppConfig(version: String, scraperConfig: ScraperConfig, httpConfig: HttpConfig, databaseConfig: DatabaseConfig, apiConfig: ApiConfig)
}
