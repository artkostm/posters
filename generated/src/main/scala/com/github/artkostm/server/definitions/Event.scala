package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Event(media: Media, name: String, description: Description)
object Event {
  implicit val encodeEvent = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct3("media", "name", "description") { (o: Event) => (o.media, o.name, o.description) }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeEvent = Decoder.forProduct3("media", "name", "description")(Event.apply _)
}