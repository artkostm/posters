package com.github.artkostm.models.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class DialogflowRequestBody(responseId: String,
                                 queryResult: QueryResult,
                                 originalDetectIntentRequest: OriginalDetectIntentRequest,
                                 session: String)
object DialogflowRequestBody {
  implicit val encodeDialogflowRequestBody = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct4("responseId", "queryResult", "originalDetectIntentRequest", "session") {
        (o: DialogflowRequestBody) =>
          (o.responseId, o.queryResult, o.originalDetectIntentRequest, o.session)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeDialogflowRequestBody =
    Decoder.forProduct4("responseId", "queryResult", "originalDetectIntentRequest", "session")(
      DialogflowRequestBody.apply _)
}
