package com.artkostm.posters.controllers

import com.artkostm.posters._
import com.artkostm.posters.model.EventsDay
import com.artkostm.posters.repository.PostgresPostersRepository
import com.google.inject.Inject
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finatra.request.{QueryParam, RouteParam}
import io.swagger.models.Swagger
import org.joda.time.DateTime

class CategoryController @Inject()(s: Swagger) extends SwaggerController
  with AllDayOfEventsOperation
  with EventsByCategoryNameOperation {

  override implicit protected val swagger = s

  implicit val ec = actorSystem.dispatcher

  getWithDoc("/posters/:date/?")(allDayOfEventsOp) { request: WithDate =>
    PostgresPostersRepository.find(request.date).map {
      case Some(EventsDay(_, categories)) => categories
      case _ => eventsScraper.scheduleFor(request.date).events
    }
  }

  getWithDoc("/posters/?")(eventsByCategoryNameOp) { request: WithNameAndDate =>
    PostgresPostersRepository.findCategory(request.date, request.name).map {
      case Some(category) => category
      case _ => eventsScraper.scheduleFor(request.date).events.filter(_.name.equalsIgnoreCase(request.name)).headOption
    }
  }
}

case class WithDate(@RouteParam date: DateTime)
case class WithNameAndDate(@QueryParam name: String, @QueryParam date: DateTime)
