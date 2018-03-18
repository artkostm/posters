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


class SimpleRepo {

  def categories(date: String): List[Category] = List(
    Category("category1", List(
      Event(Media("link1", "img1"), "event name1", Description("desc1", Some("ticket1"), true))
    )),
    Category("category2", List(
      Event(Media("link2", "img2"), "event name2", Description("desc2", None, false))
    ))
  )
}

object Schema {
  import sangria.schema._

  val MediaType = ObjectType("Media", "The media type", fields[Unit, Media](
    Field("link", StringType, resolve = _.value.link),
    Field("img", StringType, resolve = _.value.img)
  ))

  val DescriptionType = ObjectType("Description", "The description type", fields[Unit, Description](
    Field("desc", StringType, resolve = _.value.desc),
    Field("ticket", OptionType(StringType), description = Some("the ticket link"), resolve = _.value.ticket),
    Field("isFree", BooleanType, resolve = _.value.isFree)
  ))

  val EventType = ObjectType("Event", "The event type", fields[Unit, Event](
    Field("media", MediaType, resolve = _.value.media),
    Field("name", StringType, resolve = _.value.name),
    Field("description", DescriptionType, resolve = _.value.description)
  ))

  val CategoryType = ObjectType("Category", "The category type", fields[Unit, Category](
    Field("name", StringType, resolve = _.value.name),
    Field("events", ListType(EventType), resolve = _.value.events)
  ))

  val DateArgument = Argument("date", StringType)

  val QueryType = ObjectType("Query", fields[SimpleRepo, Unit](
    Field("categories", ListType(CategoryType), description = Some("Returns a list of all available products."),
      arguments = DateArgument :: Nil,
      resolve = c => c.ctx.categories(c arg DateArgument))
  ))
}