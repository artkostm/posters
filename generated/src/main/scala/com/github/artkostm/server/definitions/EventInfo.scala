package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class EventInfo(description: String,
                     photos: IndexedSeq[String] = IndexedSeq.empty,
                     comments: IndexedSeq[Comment] = IndexedSeq.empty)
object EventInfo {
  implicit val encodeEventInfo = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct3("description", "photos", "comments") { (o: EventInfo) =>
        (o.description, o.photos, o.comments)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeEventInfo = Decoder.forProduct3("description", "photos", "comments")(EventInfo.apply _)
}
