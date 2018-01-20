package com.artkostm.posters.controllers

import com.google.inject.Inject
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finatra.request.{QueryParam, RouteParam}
import io.swagger.models.Swagger
import org.joda.time.DateTime

class CategoryController @Inject()(s: Swagger) extends SwaggerController
  with AllDayOfEventsOperation
  with EventsByCategoryNameOperation {

  override implicit protected val swagger = s

  getWithDoc("/posters/:date/?")(allDayOfEventsOp) { request: WithDate =>
    s"only date: ${request.date}"
  }

  getWithDoc("/posters/?")(eventsByCategoryNameOp) { request: WithNameAndDate =>
    s"Hello, ${request.name} Кино"
  }
}

case class WithDate(@RouteParam date: DateTime)
case class WithNameAndDate(@QueryParam name: String, @QueryParam date: DateTime)
