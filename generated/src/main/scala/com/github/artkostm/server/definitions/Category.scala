package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Category(name: String, events: IndexedSeq[Event] = IndexedSeq.empty)
object Category {
  implicit val encodeCategory = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct2("name", "events") { (o: Category) => (o.name, o.events) }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeCategory = Decoder.forProduct2("name", "events")(Category.apply _)
}