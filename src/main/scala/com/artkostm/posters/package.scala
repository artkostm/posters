package com.artkostm

import java.net.URI

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
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
    config.getString("scraper.tut.imgSelector")
  ))
  lazy val httpConfig = HttpConfig(
    s":${config.getString("http.port")}"
  )
  lazy val dbConfig = {
    val uri = new URI(config.getString("db.url"))
    val user = uri.getUserInfo().split(":")(0)
    val password = uri.getUserInfo().split(":")(1)
    DbConfig(s"jdbc:postgresql://${uri.getHost()}:${uri.getPort()}${uri.getPath()}", user, password)
  }
}

case class TutScraper(url: String, format: DateTimeFormatter, blocksSelector: String,
                      blockTitleSelector: String, eventsSelector: String,
                      mediaSelector: String, eventNameSelector: String,
                      descriptionSelector: String, descriptionTextSelector: String,
                      ticketSelector: String, freeEventSelector: String,
                      hrefAttrSelector: String, srcAttrSelector: String,
                      imgSelector: String)
case class ScraperConfig(tut: TutScraper)
case class HttpConfig(port: String)
case class DbConfig(url: String, user: String, password: String)

