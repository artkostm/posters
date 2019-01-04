package com.artkostm.posters.collector

import javax.inject.Singleton
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Sink, Source}
import com.artkostm.posters.model._
import org.joda.time.DateTime
import com.artkostm.posters.repository.PostgresPostersRepository
import com.artkostm.posters.scraper.EventsScraper
import com.google.inject.Inject

import scala.concurrent.Future

/**
  * FS2 to Akka Stream example: https://github.com/krasserm/streamz/blob/master/streamz-examples/src/main/scala/streamz/examples/converter/Example.scala
  * @param scraper
  * @param repository
  * @param actorSystem
  */
@Singleton
class EventsCollector @Inject()(scraper: EventsScraper,
                                repository: PostgresPostersRepository,
                                actorSystem: ActorSystem) {

  private implicit val system = actorSystem

  private val decider: Supervision.Decider = {
    case e: Exception =>
      println("Exception handled, recovering collector stream: " + e.getMessage)
      Supervision.Restart
    case _ => Supervision.Stop
  }

  private implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem).withSupervisionStrategy(decider))

  private implicit val ec = actorSystem.dispatcher

  private val days = (-2 to 30).map(DateTime.now().plusDays).map(_.withTimeAtStartOfDay())

  private val g = Source.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val in = Source
      .fromIterator[DateTime](() => days.toIterator)
      //.throttle(5, 1 second, 1, ThrottleMode.shaping)
      .mapAsyncUnordered(5)(date => Future(scraper.scheduleFor(date)))

    val eventsInfoFlow = Flow[Day]
      .flatMapConcat(d => Source.fromIterator(() => d.events.flatMap(_.events).toIterator))
      //.throttle(5, 1 second, 1, ThrottleMode.shaping)
      .mapAsyncUnordered(5)(event => Future(scraper.eventInfo(event.media.link)))

    val fanIn = builder.add(Merge[Int](2))
    val bcast = builder.add(Broadcast[Day](2))

    val saveDayFlow =
      Flow[Day].map(day => EventsDay(day.date, Category.toJson(day.events))).mapAsyncUnordered(5)(repository.saveDay)

    val saveInfoFlow = Flow[Option[Info]].mapAsyncUnordered(5) {
      case Some(info) => repository.saveInfo(info)
      case None       => Future.failed(new RuntimeException("Can't save an event info"))
    }

    // format: off
    in ~> bcast ~> saveDayFlow                    ~> fanIn
          bcast ~> eventsInfoFlow ~> saveInfoFlow ~> fanIn
    // format: on
    SourceShape(fanIn.out)
  })

  def run(sink: Graph[SinkShape[Int], _] = Sink.ignore) = g.to(sink).run()
}
