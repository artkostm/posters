package com.artkostm.posters.collector

import java.util.Date

import akka.stream.{ActorMaterializer, FlowShape, ThrottleMode}
import akka.stream.scaladsl.{Balance, BidiFlow, Broadcast, Flow, GraphDSL, Keep, Merge, Sink, Source}
import com.artkostm.posters.model._
import org.joda.time.{DateTime, DateTimeZone}
import com.artkostm.posters._
import com.artkostm.posters.scraper.EventsScraper
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.duration._

object C1 extends App {
  val storage = scala.collection.mutable.Map.empty[String, List[Category]]
  val month = (0 to 30).map(DateTime.now().plusDays)

//  implicit val system = actorSystem
//  implicit val materializer = ActorMaterializer()
//  implicit val ec = system.dispatcher

  object Cat {
    import play.api.libs.json._
    implicit val desFmt = Json.format[Description]
    implicit val desWrt = Json.writes[Description]

    implicit val medFmt = Json.format[Media]
    implicit val medWrt = Json.writes[Media]

    implicit val evnFmt = Json.format[Event]
    implicit val evnWrt = Json.writes[Event]

    implicit val catFmt = Json.format[Category]
    implicit val catWrt = Json.writes[Category]

    implicit val dayFmt = Json.format[Day]
    implicit val dayWrt = Json.writes[Day]
  }

  import Cat._
  //println(Json.toJson(Day(List(Category("movie", List(Event(Media("", ""), "", Description("description here", Some("50$"), false))))), new Date)).toString())

  println(Json.parse(
    """
      |{"events":[{"name":"movie","events":[{"media":{"link":"http;//lisiy-korch","img":"KARTINKA"},"name":"","description":{"desc":"description here","ticket":"50$","isFree":false}}]}],"date":1512316681373}
    """.stripMargin).as[Day])

//  val scraper = new EventsScraper(scraperConfig)
//
//  val done = Source.fromIterator[DateTime](() => month.toIterator)
//    .throttle(3, 1 second, 1, ThrottleMode.shaping)
//    .mapAsyncUnordered(3)(date => Future(scraper.scheduleFor(date)))
//    .toMat(Sink.foreach(println))(Keep.right).run()
//
//  done.onComplete(_ => actorSystem.terminate())

//  import GraphDSL.Implicits._
//  val partial = GraphDSL.create() { implicit builder =>
//    val B = builder.add(Broadcast[Int](2))
//    val C = builder.add(Merge[Int](2).async)
//    val E = builder.add(Balance[Int](2))
//    val F = builder.add(Merge[Int](2))
//
//                C <~ F
//    B  ~>    Flow[Int].map(id => {println(s"B -> C: $id"); id})  ~>   C ~> F
//    B  ~>  Flow[Int].map(id => {println(s"B -> E: $id"); id})  ~>  E  ~> Flow[Int].map(id => {println(s"E -> F: $id"); id}) ~>  F
//    FlowShape(B.in, E.out(1))
//  }.named("partial")
//
//
//  val (_, done) = Source.fromIterator[Int](() => (1 to 6).toIterator)
//    .throttle(1, 1 second, 1, ThrottleMode.shaping)
//    .via(partial)
//    .toMat(Sink.foreach(println))(Keep.both).run()
//
//  done.onComplete(_ => actorSystem.terminate())
//
//  case class Message(content: String)
//
//  def toStr(message: Message): String = message.content
//  def fromStr(str: String): Message = Message(str)
//
//
//  val codec = BidiFlow.fromFunctions(toStr, fromStr)
}
