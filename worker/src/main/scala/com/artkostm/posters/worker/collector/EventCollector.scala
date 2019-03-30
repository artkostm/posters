package com.artkostm.posters.worker.collector

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import cats.effect.{Concurrent, Timer}
import com.artkostm.posters.algebra.{EventStore, InfoStore, VisitorStore}
import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.scraper.Scraper
import fs2._

class EventCollector[F[_]: Timer](scraper: Scraper[F],
                                  eventStore: EventStore[F],
                                  infoStore: InfoStore[F],
                                  visitorStore: VisitorStore[F])(implicit F: Concurrent[F]) {
  // TODO: move all magic numbers to config
  def collect() = {
    val dayStream = Stream
      .range(-2, 31)
      .covary[F]
      .map(LocalDate.now().plus(_, ChronoUnit.DAYS))
      .mapAsyncUnordered(4)(scraper.event)

    val insertDays = dayStream.mapAsyncUnordered(4)(eventStore.save)
    val saveEvents = dayStream
      .flatMap(day => Stream.emits(day.categories.flatMap(_.events)))
      .mapAsyncUnordered(4)(event => scraper.eventInfo(event.media.link))
      .chunkN(10)
      .mapAsync(4)(chunk =>
        infoStore.save(chunk.map {
          case Some(EventInfo(link, eventInfo)) => (link, eventInfo, eventInfo)
        })
      )

    Stream.eval(eventStore.deleteOld(LocalDate.now())) >>
      Stream.eval(infoStore.deleteOld()) >>
      Stream.eval(visitorStore.deleteOld(LocalDate.now())) >>
      Stream(insertDays, saveEvents).parJoin(2)
  }
}
