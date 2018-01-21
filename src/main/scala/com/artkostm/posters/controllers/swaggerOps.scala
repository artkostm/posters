package com.artkostm.posters.controllers

import com.artkostm.posters.model.{Category, Description, Event, Media}
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
      .responseWith[List[Category]](200, "singleton list of an category found", example = Some(
      List(Category(
        "CategoryName", List(
          Event(Media(
            "event link", "image link"), "event name", Description("event description", Some("ticket link"), true)))))))
      .responseWith(404, "category with the name are not found for received date")
}
