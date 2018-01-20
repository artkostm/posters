package com.artkostm.posters

import com.artkostm.posters.dialog._
import com.artkostm.posters.model._
import com.artkostm.posters.modules.{AkkaModule, DbModule, PostersSwaggerModule}
import com.artkostm.posters.repository.PostgresPostersRepository
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.inject.{Inject, Module, Singleton}
import com.jakehschwartz.finatra.swagger.{DocsController, SwaggerController}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.finatra.request.QueryParam
import io.swagger.models.Swagger
import org.joda.time.DateTime

import scala.concurrent.Future

object PostersJacksonModule extends FinatraJacksonModule {
  override protected val propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
}

class PostersServer extends HttpServer {
  override val defaultFinatraHttpPort: String = httpConfig.port
  override protected def disableAdminHttpServer = true
  override protected def jacksonModule = PostersJacksonModule
  override protected def modules: Seq[Module] = Seq(DbModule, AkkaModule, PostersSwaggerModule)
  override protected def configureHttp(router: HttpRouter): Unit =
    router
      .exceptionMapper[IllegalArgumentExceptionHandler]
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[DocsController]
      .add[ScheduleController]
}

class ScheduleController @Inject()(s: Swagger) extends SwaggerController {
  override implicit protected val swagger = s

  implicit val ec = actorSystem.dispatcher

  getWithDoc("/posters/categories/?") { o =>
    o.summary("Read category information")
      .description("Read the detail information about the student.")
      .tag("Category")
      .routeParam[String]("id", "the student id")
      .produces("application/json")
      .responseWith[List[Category]](200, "list of categories", example = Some(
        List(Category(
          "CategoryName", List(
            Event(Media(
              "event link", "image link"), "event name", Description("event description", Some("ticket link"), true)))))))
      .responseWith(404, "categories are not found")
  } { request: CategoryRequest =>
    request match {
      case CategoryRequest(Some(date), Some(category), _) => PostgresPostersRepository.find(date).map {
        case None => eventsScraper.scheduleFor(date).events.filter(_.name.equalsIgnoreCase(category))
        case Some(day) => day.categories.filter(_.name.equalsIgnoreCase(category))
      }
      case CategoryRequest(Some(date), _, Some(true)) => PostgresPostersRepository.find(date).map {
        case Some(day) => day.categories
        case None => eventsScraper.scheduleFor(date).events
      }
      case CategoryRequest(Some(date), None, None) => PostgresPostersRepository.find(date).map {
        case Some(day) => day.categories
        case None => eventsScraper.scheduleFor(date).events
      }
      case _ => Future.successful(response.badRequest)
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

  post("/posters/webhook/?") { request: DialogflowRequest =>
    logger.info(request.toString)
    if (!FlowKeyDataExtractor.actionIncomplete(request)) {
      FlowKeyDataExtractor.extract(request) match {
        case FlowKeyData(category, Some(date), _) =>
          if (FlowKeyDataExtractor.shouldShowAll(request)) PostgresPostersRepository.find(date).map {
            case Some(day) => DialogflowResponse("", ResponseData(day.categories), "posters")
            case None => DialogflowResponse("", ResponseData(eventsScraper.scheduleFor(date).events), "posters")
          } else PostgresPostersRepository.find(date).map {
            case None => DialogflowResponse("", ResponseData(eventsScraper.scheduleFor(date).events.filter(cat => category.contains(cat.name))), "posters")
            case Some(day) => DialogflowResponse("", ResponseData(day.categories.filter(cat => category.contains(cat.name))), "posters")
          }
        case FlowKeyData(category, _, Some(period)) => Future.successful(response.badRequest)
        case _ => Future.successful(response.badRequest)
      }
    } else {
      Future.successful(response.badRequest)
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
case class AssigneeRequest(@QueryParam date: DateTime,
                           @QueryParam category: String,
                           @QueryParam name: String)
case class EventInfoRequest(@QueryParam link: String)
