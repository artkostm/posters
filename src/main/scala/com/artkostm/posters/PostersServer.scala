package com.artkostm.posters

import com.artkostm.posters.controllers.CategoryController
import com.artkostm.posters.modules._
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.google.inject.{Inject, Module, Singleton}
import com.jakehschwartz.finatra.swagger.{DocsController, SwaggerController}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.finatra.request.QueryParam
import io.swagger.models.Swagger
import org.joda.time.DateTime

object PostersJacksonModule extends FinatraJacksonModule {
  override protected val propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
}

class PostersServer extends HttpServer {
  override val defaultFinatraHttpPort: String = ":8080"
  override protected def disableAdminHttpServer = true
  override protected def jacksonModule = PostersJacksonModule
  override protected def modules: Seq[Module] = Seq(ConfigModule, AkkaModule, ToolsModule, DbModule, PostersSwaggerModule)
  override protected def configureHttp(router: HttpRouter): Unit =
    router
      .exceptionMapper[IllegalArgumentExceptionHandler]
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[DocsController]
      .add[CategoryController]
}

class ScheduleController @Inject()(s: Swagger) extends SwaggerController {
  override implicit protected val swagger = s
//  post("/webhook/?") { request: DialogflowRequest =>
//    logger.info(request.toString)
//    if (!FlowKeyDataExtractor.actionIncomplete(request)) {
//      FlowKeyDataExtractor.extract(request) match {
//        case FlowKeyData(category, Some(date), _) =>
//          if (FlowKeyDataExtractor.shouldShowAll(request)) PostgresPostersRepository.find(date).map {
//            case Some(day) => DialogflowResponse("", ResponseData(day.categories), "posters")
//            case None => DialogflowResponse("", ResponseData(eventsScraper.scheduleFor(date).events), "posters")
//          } else PostgresPostersRepository.find(date).map {
//            case None => DialogflowResponse("", ResponseData(eventsScraper.scheduleFor(date).events.filter(cat => category.contains(cat.name))), "posters")
//            case Some(day) => DialogflowResponse("", ResponseData(day.categories.filter(cat => category.contains(cat.name))), "posters")
//          }
//        case FlowKeyData(category, _, Some(period)) => Future.successful(response.badRequest)
//        case _ => Future.successful(response.badRequest)
//      }
//    } else {
//      Future.successful(response.badRequest)
//    }
//  }
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
