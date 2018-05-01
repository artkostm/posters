package com.artkostm.posters.collector

import javax.inject.Singleton

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import com.artkostm.posters.model._
import org.joda.time.DateTime
import com.artkostm.posters.repository.PostgresPostersRepository
import com.artkostm.posters.scraper.EventsScraper
import com.google.inject.Inject

import scala.concurrent.Future

@Singleton
class EventsCollector @Inject()(scraper: EventsScraper, repository: PostgresPostersRepository,
                                actorSystem: ActorSystem) {
  private implicit val system = actorSystem
  private implicit val materializer = ActorMaterializer()
  private implicit val ec = actorSystem.dispatcher
  private val days = (-2 to 30).map(DateTime.now().plusDays).map(_.withTimeAtStartOfDay())

  private val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val in = Source.fromIterator[DateTime](() => days.toIterator)
      //.throttle(5, 1 second, 1, ThrottleMode.shaping)
      .mapAsyncUnordered(5)(date => Future(scraper.scheduleFor(date)))

    val eventsInfoFlow = Flow[Day]
      .flatMapConcat(d => Source.fromIterator(() => d.events.flatMap(_.events).toIterator))
      //.throttle(5, 1 second, 1, ThrottleMode.shaping)
      .mapAsyncUnordered(5)(event => Future(scraper.eventInfo(event.media.link)))

    val out = Sink.ignore
    val bcast = builder.add(Broadcast[Day](2))

    val saveDayFlow = Flow[Day].map(day => EventsDay(new DateTime(day.date), Category.toJson(day.events))).mapAsyncUnordered(5)(repository.saveDay)

    val saveInfoFlow = Flow[Option[Info]].mapAsyncUnordered(5) {
      case Some(info) => repository.saveInfo(info)
      case None => Future.failed(new RuntimeException)
    }

    in ~> bcast ~> saveDayFlow                    ~> out
          bcast ~> eventsInfoFlow ~> saveInfoFlow ~> out
    ClosedShape
  })

  def run() = g.run()
}
