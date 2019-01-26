package com.github.artkostm.models.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class DialogflowResponse(fulfillmentText: String, payload: ResponsePayload, source: String)
object DialogflowResponse {
  implicit val encodeDialogflowResponse = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct3("fulfillmentText", "payload", "source") { (o: DialogflowResponse) => (o.fulfillmentText, o.payload, o.source) }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeDialogflowResponse = Decoder.forProduct3("fulfillmentText", "payload", "source")(DialogflowResponse.apply _)
}