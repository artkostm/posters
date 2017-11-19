package com.artkostm.posters

import com.artkostm.posters.scraper.Scraper
import com.google.inject.Inject
import com.google.inject.Singleton
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.request.QueryParam
import org.joda.time.DateTime

class PostersServer extends HttpServer {
  override protected def configureHttp(router: HttpRouter): Unit =
    router
      .exceptionMapper[IllegalArgumentExceptionHandler]
      .add[ScheduleController]
}

class ScheduleController extends Controller {
  val scraper = new Scraper(scraperConfig)

  get("/posters/categories/?") { request: CategoryRequest =>
    request match {
      case CategoryRequest(Some(date), Some(category), _) => scraper.scheduleFor(date).events.filter(_.name.equalsIgnoreCase(category))
      case CategoryRequest(Some(date), _, Some(true)) => scraper.scheduleFor(date).events
      case CategoryRequest(Some(date), None, None) => scraper.scheduleFor(date).events
      case CategoryRequest(_, _, Some(false)) => scraper.scheduleFor(DateTime.now).events.map(_.name)
    }
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
