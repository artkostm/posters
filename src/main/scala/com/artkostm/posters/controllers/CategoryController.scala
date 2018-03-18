package com.artkostm.posters.controllers

import akka.actor.ActorSystem
import com.artkostm.posters.model._
import com.artkostm.posters.repository.PostgresPostersRepository
import com.artkostm.posters.scraper.EventsScraper
import com.google.inject.Inject
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finatra.request.{QueryParam, RouteParam}
import io.swagger.models.Swagger
import org.joda.time.DateTime

class CategoryController @Inject()(s: Swagger, repository: PostgresPostersRepository,
                                   system: ActorSystem, scraper: EventsScraper)
  extends SwaggerController with AllDayOfEventsOperation {

  override implicit protected val swagger = s

  private implicit val ec = system.dispatcher

  getWithDoc("/posters/:date/?")(allDayOfEventsOp) { request: WithDate =>
    repository.find(request.date).map {
      case Some(EventsDay(_, categories)) => categories
      case _ => scraper.scheduleFor(request.date).events
    }
  }

  getWithDoc("/posters/?")(eventsByCategoryNameOp) { request: WithNameAndDate =>
    repository.findCategory(request.date, request.name).map {
      case Some(category) => category
      case _ => scraper.scheduleFor(request.date).events.filter(_.name.equalsIgnoreCase(request.name)).headOption
    }
  }
}

case class WithDate(@RouteParam date: DateTime)
case class WithNameAndDate(@QueryParam name: String, @QueryParam date: DateTime)
