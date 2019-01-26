package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Description(desc: String, ticket: Option[String] = None, isFree: Boolean)
object Description {
  implicit val encodeDescription = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct3("desc", "ticket", "isFree") { (o: Description) => (o.desc, o.ticket, o.isFree) }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeDescription = Decoder.forProduct3("desc", "ticket", "isFree")(Description.apply _)
}