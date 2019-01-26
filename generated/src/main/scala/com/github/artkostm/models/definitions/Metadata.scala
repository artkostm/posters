package com.github.artkostm.models.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Metadata(intentId: String, webhookUsed: String, webhookForSlotFillingUsed: String, intentName: String)
object Metadata {
  implicit val encodeMetadata = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct4("intentId", "webhookUsed", "webhookForSlotFillingUsed", "intentName") { (o: Metadata) => (o.intentId, o.webhookUsed, o.webhookForSlotFillingUsed, o.intentName) }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeMetadata = Decoder.forProduct4("intentId", "webhookUsed", "webhookForSlotFillingUsed", "intentName")(Metadata.apply _)
}