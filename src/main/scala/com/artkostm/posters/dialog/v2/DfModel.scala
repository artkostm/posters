package com.artkostm.posters.dialog.v2

import com.artkostm.posters.model.Category
import org.joda.time.DateTime

case class Datetime(date: Option[DateTime], period: Option[String])
case class Parameters(category: List[String], datetime: Datetime)
case class Intent(name: String, displayName: String)
case class DiagnosticInfo()
case class QueryResult(queryText: String, allRequiredParamsPresent: Boolean, intent: Intent, intentDetectionConfidence: Double,
                       diagnosticInfo: DiagnosticInfo, languageCode: String)
case class Payload()
case class OriginalDetectIntentRequest(payload: Payload)
case class DislogFlowRequest(responseId: String, originalDetectIntentRequest: OriginalDetectIntentRequest, session: String)


case class ResponsePayload(categories: List[Category])
case class DialogflowResponse(fulfillmentText: String, payload: ResponsePayload, source: String)
