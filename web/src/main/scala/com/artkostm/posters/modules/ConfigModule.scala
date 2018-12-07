package com.artkostm.posters.modules

import javax.inject.Singleton

import com.google.inject.Provides
import com.twitter.inject.TwitterModule
import com.typesafe.config.{Config, ConfigFactory}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object ConfigModule extends TwitterModule {

  @Singleton @Provides def config(): Config = ConfigFactory.load()

  @Singleton @Provides def apiConfig(config: Config): ApiConfig =
    ApiConfig(config.getString("api.key"), config.getString("api.token"))

  @Singleton @Provides def appConfig(config: Config): AppConfig = AppConfig(config.getString("posters.version"))

  @Singleton @Provides def httpConfig(config: Config): HttpConfig = HttpConfig(s":${config.getString("http.port")}")

  @Singleton @Provides def scraperConfig(config: Config): ScraperConfig =
    ScraperConfig(
      TutScraper(
        config.getString("scraper.tut.url"),
        DateTimeFormat.forPattern(config.getString("scraper.tut.format")),
        config.getString("scraper.tut.blocksSelector"),
        config.getString("scraper.tut.blockTitleSelector"),
        config.getString("scraper.tut.eventsSelector"),
        config.getString("scraper.tut.mediaSelector"),
        config.getString("scraper.tut.eventNameSelector"),
        config.getString("scraper.tut.descriptionSelector"),
        config.getString("scraper.tut.descriptionTextSelector"),
        config.getString("scraper.tut.ticketSelector"),
        config.getString("scraper.tut.freeEventSelector"),
        config.getString("scraper.tut.hrefAttrSelector"),
        config.getString("scraper.tut.srcAttrSelector"),
        config.getString("scraper.tut.imgSelector"),
        config.getString("scraper.tut.eventPhotoSelector"),
        config.getString("scraper.tut.eventDescriptionSelector"),
        config.getString("scraper.tut.commentsSelector"),
        config.getString("scraper.tut.commentAuthorSelector"),
        config.getString("scraper.tut.commentDateSelector"),
        config.getString("scraper.tut.commentTextSelector")
      ))
}

case class TutScraper(url: String,
                      format: DateTimeFormatter,
                      blocksSelector: String,
                      blockTitleSelector: String,
                      eventsSelector: String,
                      mediaSelector: String,
                      eventNameSelector: String,
                      descriptionSelector: String,
                      descriptionTextSelector: String,
                      ticketSelector: String,
                      freeEventSelector: String,
                      hrefAttrSelector: String,
                      srcAttrSelector: String,
                      imgSelector: String,
                      eventPhotoSelector: String,
                      eventDescriptionSelector: String,
                      commentsSelector: String,
                      commentAuthorSelector: String,
                      commentDateSelector: String,
                      commentTextSelector: String)
case class ScraperConfig(tut: TutScraper)
case class HttpConfig(port: String)
case class ApiConfig(apiKey: String, apiToken: String)
case class AppConfig(version: String)
