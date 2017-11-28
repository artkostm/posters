package com.artkostm.posters

import com.artkostm.posters.model.Assign
import com.artkostm.posters.modules.{AkkaModule, DbModule}
import com.artkostm.posters.repository.PostgresEventsRepository
import com.artkostm.posters.scraper.Scraper
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
  val scraper = new Scraper(scraperConfig)
  implicit val ec = actorSystem.dispatcher

  get("/posters/categories/?") { request: CategoryRequest =>
    request match {
      case CategoryRequest(Some(date), Some(category), _) => scraper.scheduleFor(date).events.filter(_.name.equalsIgnoreCase(category))
      case CategoryRequest(Some(date), _, Some(true)) => scraper.scheduleFor(date).events
      case CategoryRequest(Some(date), None, None) => scraper.scheduleFor(date).events
      case CategoryRequest(_, _, Some(false)) => scraper.scheduleFor(DateTime.now).events.map(_.name)
      case _ => response.badRequest
    }
  }

  post("/posters/assignee/?") { request: Assign =>
    PostgresEventsRepository.save(request)
  }

  get("/posters/assignee/?") { request: AssigneeRequest =>
    PostgresEventsRepository.find(request.category, request.date, request.name)
  }
  
  get("/posters/eventinfo/?") { request: EventInfoRequest =>
    scraper.eventInfo(request.link)
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
