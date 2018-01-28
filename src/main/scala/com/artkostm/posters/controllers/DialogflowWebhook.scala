package com.artkostm.posters.controllers

import akka.actor.ActorSystem
import com.artkostm.posters.ErrorResponse
import com.artkostm.posters.dialog._
import com.artkostm.posters.repository.PostgresPostersRepository
import com.artkostm.posters.scraper.EventsScraper
import com.google.inject.Inject
import com.jakehschwartz.finatra.swagger.SwaggerController
import io.swagger.models.Swagger

import scala.concurrent.Future

class DialogflowWebhook @Inject() (s: Swagger, repository: PostgresPostersRepository,
                                   system: ActorSystem, scraper: EventsScraper)
  extends SwaggerController with DialogflowWebhookOperation {
  override implicit protected val swagger = s

  private implicit val ec = system.dispatcher

  postWithDoc("/webhook/?")(webhookOp) { request: DialogflowRequest =>
    if (!FlowKeyDataExtractor.actionIncomplete(request))
      FlowKeyDataExtractor.extract(request) match {
        case FlowKeyData(categories, Some(date), _) =>
          if (FlowKeyDataExtractor.shouldShowAll(request)) repository.find(date).map {
            case Some(day) =>
              DialogflowResponse("", ResponseData(day.categories), "posters")
            case None =>
              DialogflowResponse("", ResponseData(scraper.scheduleFor(date).events), "posters")
          } else repository.findCategories(date, categories).map {
            case IndexedSeq() =>
              DialogflowResponse("", ResponseData(scraper.scheduleFor(date).events.filter(cat => categories.contains(cat.name))), "posters")
            case nonEmpty =>
              DialogflowResponse("", ResponseData(nonEmpty.toList), "posters")
          }
        case FlowKeyData(categories, _, Some(period)) =>
          repository.findCategories(FlowKeyDataExtractor.getPeriod(period), categories)
        case _ => Future.successful(response.badRequest(ErrorResponse("1", "cannot extract key data")))
      }
    else Future.successful(response.badRequest(ErrorResponse("1", "action is incomplete")))
  }
}
