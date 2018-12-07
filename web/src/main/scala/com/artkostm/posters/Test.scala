package com.artkostm.posters

import akka.actor.ActorSystem
import com.artkostm.posters.collector.EventsCollector
import com.artkostm.posters.modules.{ScraperConfig, TutScraper}
import com.artkostm.posters.scraper.EventsScraper
import com.typesafe.config.ConfigFactory
import org.joda.time.format.DateTimeFormat
import tsec.jws.mac.{JWTMac, JWTMacImpure}
import tsec.jwt.JWTClaims
import tsec.mac.jca.HMACSHA256

object Test extends App {

//  val system = ActorSystem("test")
//
//  val config = ConfigFactory.load()
//  val sconfig = ScraperConfig(TutScraper(
//    config.getString("scraper.tut.url"),
//    DateTimeFormat.forPattern(config.getString("scraper.tut.format")),
//    config.getString("scraper.tut.blocksSelector"),
//    config.getString("scraper.tut.blockTitleSelector"),
//    config.getString("scraper.tut.eventsSelector"),
//    config.getString("scraper.tut.mediaSelector"),
//    config.getString("scraper.tut.eventNameSelector"),
//    config.getString("scraper.tut.descriptionSelector"),
//    config.getString("scraper.tut.descriptionTextSelector"),
//    config.getString("scraper.tut.ticketSelector"),
//    config.getString("scraper.tut.freeEventSelector"),
//    config.getString("scraper.tut.hrefAttrSelector"),
//    config.getString("scraper.tut.srcAttrSelector"),
//    config.getString("scraper.tut.imgSelector"),
//    config.getString("scraper.tut.eventPhotoSelector"),
//    config.getString("scraper.tut.eventDescriptionSelector"),
//    config.getString("scraper.tut.commentsSelector"),
//    config.getString("scraper.tut.commentAuthorSelector"),
//    config.getString("scraper.tut.commentDateSelector"),
//    config.getString("scraper.tut.commentTextSelector")
//  ))
//
//  val scraper = new EventsScraper(sconfig)
//
//  val collector = new EventsCollector(scraper, null, system)
//
//  collector.run()
//
//  Thread.sleep(60000)
//
//  println(scraper.eventInfo("https://afisha.tut.by/film/dedpul-2/").map(_.eventInfo.photos))
//  import io.circe._
//  import io.circe.parser._
//  import io.circe.syntax._
//  import io.circe.generic.auto._
//
//  case class Person(name: String)
//
//  println(Person("Artsiom").asJson)

  val ApiKey   = "some api key"
  val ApiToken = "some api token"

  val jwtKey = HMACSHA256.unsafeBuildKey(ApiToken.getBytes)

  val claims = JWTClaims(subject = Some(ApiKey), expiration = None)

  println(JWTMacImpure.buildToString(claims, jwtKey))
}
