package com.artkostm.posters.graphql

import com.artkostm.posters.controllers.SimpleRepo
import com.artkostm.posters.model.{Category, Description, Event, Media}
import sangria.validation.ValueCoercionViolation
import sangria.schema._

object TestSchema {

  case object DateCoercionViolation extends ValueCoercionViolation("Date value expected")

  val MediaType = ObjectType("Media",
                             "The media type",
                             fields[Unit, Media](
                               Field("link", StringType, resolve = _.value.link),
                               Field("img", StringType, resolve = _.value.img)
                             ))

  val DescriptionType = ObjectType(
    "Description",
    "The description type",
    fields[Unit, Description](
      Field("desc", StringType, resolve = _.value.desc),
      Field("ticket", OptionType(StringType), description = Some("the ticket link"), resolve = _.value.ticket),
      Field("isFree", BooleanType, resolve = _.value.isFree)
    )
  )

  val EventType = ObjectType(
    "Event",
    "The event type",
    fields[Unit, Event](
      Field("media", MediaType, resolve = _.value.media),
      Field("name", StringType, resolve = _.value.name),
      Field("description", DescriptionType, resolve = _.value.description)
    )
  )

  val CategoryType = ObjectType(
    "Category",
    "The category type",
    fields[Unit, Category](
      Field("name", StringType, resolve = _.value.name),
      Field("events", ListType(EventType), resolve = _.value.events)
    )
  )

  val DateArgument = Argument("date", StringType)

  val QueryType = ObjectType(
    "Query",
    fields[SimpleRepo, Unit](
      Field("categories",
            ListType(CategoryType),
            description = Some("Returns a list of all available products."),
            resolve = c => c.ctx.categories())
    )
  )

  val instance = Schema(QueryType)
}
