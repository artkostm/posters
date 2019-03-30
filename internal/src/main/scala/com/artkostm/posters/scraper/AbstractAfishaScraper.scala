package com.artkostm.posters.scraper

import java.time.LocalDate

import cats.effect.Sync
import cats.syntax.functor._
import com.artkostm.posters.Configuration.ScraperConfig
import com.artkostm.posters.interfaces.event.{Comment, EventData, EventInfo}
import com.artkostm.posters.interfaces.schedule._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model._
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor

abstract class AbstractAfishaScraper[F[_]: Sync](config: ScraperConfig) extends Scraper[F] {
  private lazy val commentExtractor: HtmlExtractor[Element, List[Comment]] =
    _.map { comment =>
      Comment(
        comment >> text(config.tut.commentAuthorSelector),
        comment >> text(config.tut.commentDateSelector),
        comment >> text(config.tut.commentTextSelector),
        comment >?> text(config.tut.commentRatingSelector)
      )
    }.toList

  private lazy val eventImagesExtractor: HtmlExtractor[Element, List[String]] =
    _.map(_ >> attr(config.tut.hrefAttrSelector)).toList

  private lazy val eventExtractor: Element => List[Option[Event]] =
    _ >> elementList(config.tut.eventsSelector) map (event =>
      for {
        media <- event >?> element(config.tut.mediaSelector)
        name  <- event >?> element(config.tut.eventNameSelector)
        txt   <- event >?> element(config.tut.descriptionSelector)
      } yield
        Event(
          name.text,
          Media(
            media.attr(config.tut.hrefAttrSelector),
            media >> attr(config.tut.srcAttrSelector)(config.tut.imgSelector)
          ),
          Description(
            txt >> text(config.tut.descriptionTextSelector),
            txt >?> attr(config.tut.hrefAttrSelector)(config.tut.ticketSelector),
            (txt >?> text(config.tut.freeEventSelector)).isDefined
          )
        ))

  def eventInfo(link: String): F[Option[EventInfo]] =
    load(link) map { doc =>
      for {
        images              <- doc >?> extractor(config.tut.eventPhotoSelector, eventImagesExtractor)
        description: String = doc >> text(config.tut.eventDescriptionSelector)
        comments            <- doc >?> extractor(config.tut.commentsSelector, commentExtractor)
      } yield EventInfo(link, EventData(description, images, comments))
    }

  def event(day: LocalDate): F[Day] =
    load(day) map { doc =>
      val categories = doc >> elementList(config.tut.blocksSelector) map { block =>
        Category(block >> text(config.tut.blockTitleSelector), eventExtractor(block).flatten)
      }
      Day(day, categories)
    }

  protected def load(day: LocalDate): F[Document]
  protected def load(link: String): F[Document]
}
