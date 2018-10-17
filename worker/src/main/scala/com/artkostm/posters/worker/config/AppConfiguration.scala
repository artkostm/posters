package com.artkostm.posters.worker.config

import cats.effect.IO
import ciris._
import ciris.refined._
import ciris.enumeratum._
import com.artkostm.posters.Configuration
import com.artkostm.posters.Configuration.DatabaseConfig
import com.artkostm.posters.environments.AppEnvironment
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object AppConfiguration extends Configuration[IO, AppConfig]{

  import com.artkostm.posters.environments.AppEnvironment._

  override protected def config =
    withValue(env[AppEnvironment]("APP_ENV").orElse(ConfigValue(Right(Local)))) {
      case Local => loadConfig {
        AppConfig(version = Configuration.APP_VERSION,
          scraper = scraperConfig,
          db = Configuration.buildDbConfig("url"))
      }
      case Production | Heroku => loadConfig(
        env[NonEmptyString]("JDBC_DATABASE_URL"),
        env[NonEmptyString]("JDBC_DATABASE_USERNAME"),
        env[NonEmptyString]("JDBC_DATABASE_PASSWORD")) { (dbUrl, user, password) =>
        AppConfig(version = Configuration.APP_VERSION,
          scraper = scraperConfig,
          db = Configuration.buildDbConfigForHeroku(dbUrl, user, password))
      }
    }

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
    commentTextSelector = ".comment_txt",
    commentRatingSelector = ".rating"
  ))
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
                      commentTextSelector: String, commentRatingSelector: String)
case class ScraperConfig(tut: TutScraper)
case class AppConfig(version: String, scraper: ScraperConfig, db: DatabaseConfig)