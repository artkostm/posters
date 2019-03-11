package com.artkostm.posters.worker.collector

import java.time.Instant
import java.time.temporal.ChronoUnit

import cats.effect.{Concurrent, Timer}
import com.artkostm.posters.algebra.{EventStore, InfoStore, VisitorStore}
import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.scraper.Scraper
import fs2.Stream

import scala.concurrent.duration._

class EventCollector[F[_]: Timer](scraper: Stream[F, Scraper[F]],
                                  eventStore: EventStore[F],
                                  infoStore: InfoStore[F],
                                  visitorStore: VisitorStore[F])(implicit F: Concurrent[F]) {
  def collect() = {
    val scr: Stream[F, Scraper[F]] = ???

    val dayStream = Stream
      .range(-2, 31)
      .covary[F]
      .map(Instant.now().plus(_, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS))
      .flatMap(i => scr.mapAsyncUnordered(4)(_.event(i)))
//      .mapAsyncUnordered(4)(scraper.event)

    val insertDays = dayStream.mapAsyncUnordered(4)(eventStore.save)
    val saveEvents = dayStream
      .flatMap(day => Stream.emits(day.categories.flatMap(_.events)))
      .flatMap(event => scraper.mapAsyncUnordered(4)(_.eventInfo(event.media.link)))
//      .filter {
//        case Some(_) => true
//        case _       => false
//      }
      .mapAsync(4) {
        case Some(info) => infoStore.save(info)
        case None       => F.raiseError[EventInfo](new RuntimeException("cannot save empty event info"))
      }

    val graph = Stream.eval(eventStore.deleteOld(Instant.now())) >>
      Stream.eval(infoStore.deleteOld()) >>
      Stream.eval(visitorStore.deleteOld(Instant.now())) >>
      Stream(insertDays, saveEvents).parJoin(2)
    graph.merge(Stream.awakeEvery[F](24 hours) >> graph)
  }
}
