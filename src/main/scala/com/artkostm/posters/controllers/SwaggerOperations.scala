package com.artkostm.posters.controllers

import com.artkostm.posters.dialog.{DialogflowRequest, DialogflowResponse, ResponseData}
import com.artkostm.posters.model._
import com.jakehschwartz.finatra.swagger.SwaggerController
import io.swagger.models.Operation
import org.joda.time.DateTime

trait AllDayOfEventsOperation { self: SwaggerController =>
  def allDayOfEventsOp(o: Operation): Operation =
    o.summary("Get all events for one day")
      .description("Get all events for one day.")
      .tag("Category")
      .routeParam[String]("date", "the date in the format yyyy-mm-dd")
      .produces("application/json")
      .responseWith[List[Category]](200, "list of categories", example = Some(
      List(Category(
        "CategoryName", List(
          Event(Media(
            "event link", "image link"), "event name", Description("event description", Some("ticket link"), true)))))))
      .responseWith(404, "categories are not found for received date")

  def eventsByCategoryNameOp(o: Operation): Operation =
    o.summary("Get all events by category name for one day")
      .description("Get all events by category name for one day")
      .tag("Category")
      .queryParam[String]("date", "the date in the format yyyy-mm-dd")
      .queryParam[String]("name", "the name of requested category")
      .produces("application/json")
      .responseWith[Category](200, "an category found with the name", example = Some(
      Category(
        "CategoryName", List(
          Event(Media(
            "event link", "image link"), "event name", Description("event description", Some("ticket link"), true))))))
      .responseWith(404, "category with the name are not found for received date")
}

trait EventInfoByLinkOperation { self: SwaggerController =>
  def eventInfoByLinkOp(o: Operation): Operation =
    o.summary("Get an event using direct link to it")
      .description("Get an event busing direct link to it.")
      .tag("Event")
      .queryParam[String]("link", "the event link in the format https://.../")
      .produces("application/json")
      .responseWith[Info](200, "singleton list of an category found", example = Some(Info("https://link.to/the/specified/event/",
        EventInfo("event detailed description", List("https://links.to/the/event/photo/"), List(Comment("author", "date", "comment text"))))))
      .responseWith(404, "event is not found")
}

trait IntentOperation { self: SwaggerController =>
  def getIntentOp(o: Operation): Operation =
    o.summary("Get an intent using event name, category name and date")
      .description("Get an intent using event name, category name and date.")
      .tag("Intent")
      .queryParam[String]("date", "the date in the format yyyy-mm-dd")
      .queryParam[String]("category", "the name of category that cantains the event")
      .queryParam[String]("name", "the name of the event")
      .produces("application/json")
      .responseWith[Assign](200, "an intent", example = Some(Assign("category name", DateTime.now, "event name", "1,2,4,6,8")))
      .responseWith(404, "intent is not found")

  def saveIntentOp(o: Operation): Operation =
    o.summary("Save or update an intent")
      .description("Save or update an intent")
      .tag("Intent")
      .request[Assign]
      .produces("application/json")
      .responseWith[Assign](200, "a new intent created", example = Some(Assign("category name", DateTime.now, "event name", "1,2,4,6,8")))
}

trait DialogflowWebhookOperation { self: SwaggerController =>
  def webhookOp(o: Operation): Operation =
    o.summary("Get all events by category name for the requested date")
      .description("Get all events by category name for the requested date.")
      .tag("Dialogflow")
      .request[DialogflowRequest]
      .produces("application/json")
      .responseWith[DialogflowResponse](200, "list of categories in dialog flow specific response",
        example = Some(DialogflowResponse("speech", ResponseData(List(Category(
          "CategoryName", List(
            Event(Media(
              "event link", "image link"), "event name", Description("event description", Some("ticket link"), true)))))), "posters source")))
      .responseWith(404, "events are not found")
      .responseWith(400, "cannot extract key data from request or action is incomplete")
}
