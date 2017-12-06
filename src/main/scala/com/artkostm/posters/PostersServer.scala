package com.artkostm.posters

import com.artkostm.posters.model.{Assign, EventsDay}
import com.artkostm.posters.modules.{AkkaModule, DbModule}
import com.artkostm.posters.repository.PostgresPostersRepository
import com.google.inject.{Inject, Module, Singleton}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.request.QueryParam
import org.joda.time.DateTime

class PostersServer extends HttpServer {
  override val defaultFinatraHttpPort: String = httpConfig.port
  override protected def modules: Seq[Module] = Seq(DbModule, AkkaModule)
  override protected def configureHttp(router: HttpRouter): Unit =
    router
      .exceptionMapper[IllegalArgumentExceptionHandler]
      .add[ScheduleController]
}

class ScheduleController extends Controller {
  implicit val ec = actorSystem.dispatcher

  get("/posters/categories/?") { request: CategoryRequest =>
    println(request)
    request match {
      case CategoryRequest(Some(date), Some(category), _) => PostgresPostersRepository.find(date).map {d => println(d);d }.map {
        case None | Some(EventsDay(_, List())) =>
          println("using scraper")
          eventsScraper.scheduleFor(date).events.filter(_.name.equalsIgnoreCase(category))
        case Some(day) => 
          println(day)
          day.categories.filter(_.name.equalsIgnoreCase(category))
      }
      case CategoryRequest(Some(date), _, Some(true)) => PostgresPostersRepository.find(date).map {
        case Some(day) => day.categories
        case None => eventsScraper.scheduleFor(date).events
      }
      case CategoryRequest(Some(date), None, None) => PostgresPostersRepository.find(date).map {
        case Some(day) => day.categories
        case None => eventsScraper.scheduleFor(date).events
      }
      case _ => response.badRequest
    }
  }

  post("/posters/assignee/?") { request: Assign =>
    PostgresPostersRepository.save(request)
  }

  get("/posters/assignee/?") { request: AssigneeRequest =>
    PostgresPostersRepository.find(request.category, request.date, request.name)
  }

  get("/posters/info/?") { request: EventInfoRequest =>
    PostgresPostersRepository.find(request.link)
  }
  
  get("/posters/eventinfo/?") { request: EventInfoRequest =>
    eventsScraper.eventInfo(request.link)
  }
}

@Singleton
class IllegalArgumentExceptionHandler @Inject() (response: ResponseBuilder)
  extends ExceptionMapper[Exception] {
  override def toResponse(request: Request, throwable: Exception): Response = {
    response.badRequest(ErrorResponse("1", throwable.getMessage))
  }
}

case class CategoryRequest(@QueryParam date: Option[DateTime],
                           @QueryParam category: Option[String],
                           @QueryParam full: Option[Boolean])
case class ErrorResponse(code: String, message: String)
case class AssigneeRequest(@QueryParam date: DateTime,
                           @QueryParam category: String,
                           @QueryParam name: String)
case class EventInfoRequest(@QueryParam link: String)
