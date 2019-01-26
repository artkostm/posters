package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class IntentBody(isUser: Boolean, date: java.time.OffsetDateTime, eventName: String, id: String)
object IntentBody {
  implicit val encodeIntentBody = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct4("isUser", "date", "eventName", "id") { (o: IntentBody) =>
        (o.isUser, o.date, o.eventName, o.id)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeIntentBody = Decoder.forProduct4("isUser", "date", "eventName", "id")(IntentBody.apply _)
}
