package com.artkostm.posters

import com.artkostm.posters.controllers.{CategoryController, DialogflowWebhook, EventInfoController, IntentsController}
import com.artkostm.posters.modules._
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.google.inject.{Inject, Module, Singleton}
import com.jakehschwartz.finatra.swagger.DocsController
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.modules.FinatraJacksonModule

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
      .add[EventInfoController]
      .add[IntentsController]
      .add[DialogflowWebhook]
}

@Singleton
class IllegalArgumentExceptionHandler @Inject() (response: ResponseBuilder)
  extends ExceptionMapper[Exception] {
  override def toResponse(request: Request, throwable: Exception): Response = {
    response.badRequest(ErrorResponse("1", throwable.getMessage))
  }
}

case class ErrorResponse(code: String, message: String)
