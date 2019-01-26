package com.github.artkostm.models.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Fulfillment(speech: String, messages: IndexedSeq[Message] = IndexedSeq.empty)
object Fulfillment {
  implicit val encodeFulfillment = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct2("speech", "messages") { (o: Fulfillment) =>
        (o.speech, o.messages)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeFulfillment = Decoder.forProduct2("speech", "messages")(Fulfillment.apply _)
}
