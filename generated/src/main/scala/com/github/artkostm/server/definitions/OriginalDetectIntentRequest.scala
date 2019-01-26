package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class OriginalDetectIntentRequest(payload: io.circe.Json)
object OriginalDetectIntentRequest {
  implicit val encodeOriginalDetectIntentRequest = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct1("payload") { (o: OriginalDetectIntentRequest) => o.payload }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeOriginalDetectIntentRequest = Decoder.forProduct1("payload")(OriginalDetectIntentRequest.apply _)
}