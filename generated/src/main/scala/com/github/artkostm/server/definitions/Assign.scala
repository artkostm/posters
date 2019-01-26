package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Assign(date: java.time.OffsetDateTime, eventName: String, vIds: IndexedSeq[String] = IndexedSeq.empty, uIds: IndexedSeq[String] = IndexedSeq.empty)
object Assign {
  implicit val encodeAssign = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct4("date", "eventName", "vIds", "uIds") { (o: Assign) => (o.date, o.eventName, o.vIds, o.uIds) }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeAssign = Decoder.forProduct4("date", "eventName", "vIds", "uIds")(Assign.apply _)
}