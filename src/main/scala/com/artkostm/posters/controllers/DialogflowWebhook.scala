package com.artkostm.posters.controllers

import akka.actor.ActorSystem
import com.artkostm.posters.ErrorResponse
import com.artkostm.posters.dialog._
import com.artkostm.posters.dialog.v1.{DialogflowResponse => DFResponseV1, ResponseData, DialogflowRequest => DFRequestV1}
import com.artkostm.posters.dialog.v2.{ResponsePayload, DialogflowRequest => DFRequestV2, DialogflowResponse => DFResponseV2}
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

  postWithDoc("/webhook/v1/?")(webhookOpV1) { request: DFRequestV1 =>
    if (!FlowKeyDataExtractor.actionIncomplete(request))
      FlowKeyDataExtractor.extract(request) match {
        case FlowKeyData(categories, Some(date), _, _) =>
          if (FlowKeyDataExtractor.shouldShowAll(request)) repository.findDay(date).map {
            case Some(day) =>
              DFResponseV1("", ResponseData(day.categories), "posters")
            case None =>
              DFResponseV1("", ResponseData(scraper.scheduleFor(date).events), "posters")
          } else repository.findCategories(date, categories).map {
            case IndexedSeq() =>
              DFResponseV1("", ResponseData(scraper.scheduleFor(date).events.filter(cat => categories.contains(cat.name))), "posters")
            case nonEmpty =>
              DFResponseV1("", ResponseData(nonEmpty.toList), "posters")
          }
        case FlowKeyData(categories, _, Some(period), _) =>
          val (startDate, endDate) = FlowKeyDataExtractor.getPeriod(period)
          repository.findCategories(startDate, endDate, categories).map { events =>
            DFResponseV1("", ResponseData(events.toList), "posters")
          }
        case _ => Future.successful(response.badRequest(ErrorResponse("1", "cannot extract key data")))
      }
    else Future.successful(response.badRequest(ErrorResponse("1", "action is incomplete")))
  }

  postWithDoc("/webhook/v2/?")(webhookOpV2) { request: DFRequestV2 =>
    if (!FlowKeyDataExtractor.actionIncomplete(request))
      FlowKeyDataExtractor.extract(request) match {
        case FlowKeyData(categories, Some(date), _, _) =>
          if (FlowKeyDataExtractor.shouldShowAll(request)) repository.findDay(date).map {
            case Some(day) =>
              DFResponseV2("", ResponsePayload(day.categories), "posters")
            case None =>
              DFResponseV2("", ResponsePayload(scraper.scheduleFor(date).events), "posters")
          } else repository.findCategories(date, categories).map {
            case IndexedSeq() =>
              DFResponseV2("", ResponsePayload(scraper.scheduleFor(date).events.filter(cat => categories.contains(cat.name))), "posters")
            case nonEmpty =>
              DFResponseV2("", ResponsePayload(nonEmpty.toList), "posters")
          }
        case FlowKeyData(categories, _, _, Some(period)) =>
          val (startDate, endDate) = FlowKeyDataExtractor.getPeriod(period)
          repository.findCategories(startDate, endDate, categories).map { events =>
            DFResponseV2("", ResponsePayload(events.toList), "posters")
          }
        case _ => Future.successful(response.badRequest(ErrorResponse("1", "cannot extract key data")))
      }
    else Future.successful(response.badRequest(ErrorResponse("1", "action is incomplete")))
  }
}
