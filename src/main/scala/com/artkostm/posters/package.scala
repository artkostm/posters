package com.artkostm

import java.net.URI

import akka.actor.ActorSystem
import com.artkostm.posters.collector.EventsCollector
import com.artkostm.posters.repository.PostgresPostersRepository
import com.artkostm.posters.scraper.EventsScraper
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

package object posters {
  val DB_NAME = "postersMap"
  lazy val actorSystem = ActorSystem("posters")
  lazy val config = ConfigFactory.load()
  lazy val scraperConfig = ScraperConfig(TutScraper(
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
  lazy val httpConfig = HttpConfig(
    s":${config.getString("http.port")}"
  )
  lazy val postgresDS = {
    val uri = new URI(config.getString("postgres.url"))
    val user = uri.getUserInfo().split(":")(0)
    val password = uri.getUserInfo().split(":")(1)
    val c = new HikariConfig()
    c.setJdbcUrl(s"jdbc:postgresql://${uri.getHost()}:${uri.getPort()}${uri.getPath()}")
    c.setUsername(user)
    c.setPassword(password)
    c.setMaximumPoolSize(19)
    c.setConnectionTimeout(120000)
    new HikariDataSource(c)
  }
  lazy val eventsScraper = new EventsScraper(scraperConfig)
  lazy val eventsCollector = new EventsCollector(eventsScraper,
    (0 to 30).map(DateTime.now().plusDays).map(_.withTimeAtStartOfDay()),
    PostgresPostersRepository)
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
case class HttpConfig(port: String)

