package com.artkostm.posters.controllers

import com.artkostm.posters.model._
import com.jakehschwartz.finatra.swagger.SwaggerController
import io.swagger.models.Operation

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
}

trait EventsByCategoryNameOperation { self: SwaggerController =>
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

trait GetIntentOperation { self: SwaggerController =>
  def getIntentOp(o: Operation): Operation =
    o.summary("Get an event using direct link to it")
      .description("Get an event busing direct link to it.")
      .tag("Event")
      .queryParam[String]("link", "the event link in the format https://.../")
      .produces("application/json")
      .responseWith[Info](200, "singleton list of an category found", example = Some(Info("https://link.to/the/specified/event/",
      EventInfo("event detailed description", List("https://links.to/the/event/photo/"), List(Comment("author", "date", "comment text"))))))
      .responseWith(404, "event is not found")
}

trait SaveIntentOperation { self: SwaggerController =>
  def saveIntentOp(o: Operation): Operation =
    o.summary("Get an event using direct link to it")
      .description("Get an event busing direct link to it.")
      .tag("Event")
      .queryParam[String]("link", "the event link in the format https://.../")
      .produces("application/json")
      .responseWith[Info](200, "singleton list of an category found", example = Some(Info("https://link.to/the/specified/event/",
      EventInfo("event detailed description", List("https://links.to/the/event/photo/"), List(Comment("author", "date", "comment text"))))))
      .responseWith(404, "event is not found")
}
