package com.artkostm.posters.scraper

import com.artkostm.ScraperConfig
import com.artkostm.posters.model._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model._
import org.joda.time.DateTime

class Scraper(config: ScraperConfig) {
  val format = config.tut.format
  val browser = JsoupBrowser()

  def scheduleFor(day: DateTime): Day = {
    val doc = browser.get(s"${config.tut.url}${format.print(day)}")
    val blocks = doc >> elementList(config.tut.blocksSelector)

    val categories = for {
      block <- blocks
    } yield Category(block >> text(config.tut.blockTitleSelector), extractEvents(block).flatten)

    Day(categories, day.toDate)
  }
  
  def eventInfo(link: String): Option[EventInfo] = {
    val doc = browser.get(link)
    for {
      images <- doc >?> elementList(config.tut.eventPhotoSelector) >> attr(config.tut.hrefAttrSelector)
      description: String = doc >> text(config.tut.eventDescriptionSelector)
      comments <- doc >?> elementList(config.tut.commentsSelector)
    } yield EventInfo(description, images, extractComments(comments))
  }

  private[this] def extractEvents(block: Element): List[Option[Event]] = {
    block >> elementList(config.tut.eventsSelector) map { event =>
      for {
        media <- event >?> element(config.tut.mediaSelector)
        name <- event >?> element(config.tut.eventNameSelector)
        txt <- event >?> element(config.tut.descriptionSelector)
      } yield Event(
        Media(media.attr(config.tut.hrefAttrSelector),
          media >> attr(config.tut.srcAttrSelector)(config.tut.imgSelector)),
        name.text,
        Description(txt >> text(config.tut.descriptionTextSelector),
          txt >?> attr(config.tut.hrefAttrSelector)(config.tut.ticketSelector),
          (txt >?> text(config.tut.freeEventSelector)).isDefined)
      )
    }
  }
  
  private[this] def extractComments(comments: List[Element]): List[Comment] =
    comments map { comment =>
      Comment(comment >> text(config.tut.commentAuthorSelector),
        comment >> text(config.tut.commentDateSelector),
        comment >> text(config.tut.commentTextSelector))
    }
}
