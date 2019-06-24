package com.artkostm.posters.worker.collector

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import cats.data.NonEmptyList
import cats.effect.{Concurrent, Timer}
import cats.implicits._
import com.artkostm.posters.algebra.{EventStore, InfoStore, VisitorStore}
import com.artkostm.posters.worker.logging.mdcF
import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.interfaces.schedule.Day
import com.artkostm.posters.scraper.EventScraper
import fs2._
import org.log4s.getLogger

class EventCollector[F[_]: Timer](scrapers: NonEmptyList[EventScraper[F]],
                                  eventStore: EventStore[F],
                                  infoStore: InfoStore[F],
                                  visitorStore: VisitorStore[F])(implicit F: Concurrent[F]) {
  private[this] val logger = getLogger("Collector")

  // TODO: move all magic numbers to config
  // TODO: add logging
  def collect(days: Seq[LocalDate]) = {

    def collectWith(scraper: EventScraper[F]): Stream[F, Any] = {
      println("Scrapping for: " + scraper)
      val dayStream = Stream
        .range(-2, 31)
        .covary[F]
        .map(LocalDate.now().plus(_, ChronoUnit.DAYS))
        .mapAsyncUnordered(4)(scraper.event(_))

      val insertDays = dayStream.mapAsyncUnordered(4) { day =>
        eventStore.save(day) <* mdcF[F](
          "Day" -> s"""{"date":"${day.eventDate}", "categories":[${day.categories
            .map(_.name)
            .mkString("\"", "\",\"", "\"")}]}"""
        )(logger.info("Saving day."))
      }.handleErrorWith { error =>
        Stream.eval(mdcF[F]() {
          logger.error(error)("")
        }) >> Stream.empty[F, Day]
      }
      val saveEvents = dayStream
        .flatMap(day => Stream.emits(day.categories.flatMap(_.events)))
        .mapAsyncUnordered(4)(event => scraper.eventInfo(event.media.link))
        .chunkN(10)
        .mapAsync(4)(chunk =>
          infoStore.save(chunk.map {
            case Some(EventInfo(link, eventInfo)) => (link, eventInfo, eventInfo)
          }))

      Stream.eval(eventStore.deleteOld(LocalDate.now())) >>
        Stream.eval(infoStore.deleteOld()) >>
        Stream.eval(visitorStore.deleteOlderThan(LocalDate.now())) >>
        Stream(insertDays, saveEvents).parJoin(2)
    }

    for {
      scraper <- Stream.emits[F, EventScraper[F]](scrapers.toList)
      _       <- collectWith(scraper)
    } yield ()
  }
}
