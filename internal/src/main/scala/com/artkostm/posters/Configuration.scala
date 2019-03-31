package com.artkostm.posters

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

import cats.implicits._
import cats.effect._
import cats.MonadError
import ciris._
import com.artkostm.posters.Configuration.{ScraperConfig, TutScraper}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.auto._

trait Configuration[F[_], Conf] {
  def load(implicit me: MonadError[F, Throwable]): F[Conf] = config flatMap {
    case Right(c)     => me.pure(c)
    case Left(errors) => me.raiseError(errors.toException)
  }

  protected def config: F[Either[ConfigErrors, Conf]]

  protected val scraperConfig = ScraperConfig(
    TutScraper(
      url = "https://afisha.tut.by/day/",
      format = DateTimeFormatter
        .ofPattern("yyyy/MM/dd")
        .withLocale(Locale.ENGLISH)
        .withZone(ZoneId.of("Europe/Minsk")),
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

object Configuration {
  val AppVersion = "3.0.0"

  val buildDbConfig: String => DatabaseConfig =
    DatabaseConfig(_, driver = "org.postgresql.Driver", user = "test", password = "12345")

  val buildDbConfigForHeroku: (String, NonEmptyString, NonEmptyString) => DatabaseConfig =
    (url, user, password) => DatabaseConfig(url, driver = "org.postgresql.Driver", user = user, password = password)

  final case class DatabaseConfig(
      url: String,
      driver: NonEmptyString,
      user: String = "",
      password: String = "",
      numThreads: PosInt = 32,
      maxConnections: PosInt = 15,
      minConnections: PosInt = 14
  )

  object DatabaseConfig {
    def transactor[F[_]: Async: ContextShift](dbConfig: DatabaseConfig): Resource[F, HikariTransactor[F]] =
      for {
        ce <- ExecutionContexts.fixedThreadPool[F](dbConfig.numThreads.value)
        te <- ExecutionContexts.cachedThreadPool[F]
        xa <- HikariTransactor.newHikariTransactor[F](driverClassName = dbConfig.driver.value,
                                                      url = dbConfig.url,
                                                      user = dbConfig.user,
                                                      pass = dbConfig.password,
                                                      connectEC = ce,
                                                      transactEC = te)
      } yield xa
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
                        commentTextSelector: String,
                        commentRatingSelector: String)
  case class ScraperConfig(tut: TutScraper)
}
