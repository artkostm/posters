package com.artkostm.posters.interfaces.dialog.v2

import com.artkostm.posters.interfaces.schedule.Category
import org.joda.time.DateTime

// for request
case class Period(endDate: DateTime, startDate: DateTime)

case class Datetime(date: Option[DateTime], period: Option[Period])

case class Parameters(category: List[String], datetime: Datetime)

case class Intent(name: String, displayName: String)

case class DiagnosticInfo()

case class QueryResult(queryText: String,
                       parameters: Parameters,
                       allRequiredParamsPresent: Boolean,
                       intent: Intent,
                       intentDetectionConfidence: Double,
                       diagnosticInfo: DiagnosticInfo,
                       languageCode: String)

case class Payload()

case class OriginalDetectIntentRequest(payload: Payload)

case class DialogflowRequest(responseId: String,
                             queryResult: QueryResult,
                             originalDetectIntentRequest: OriginalDetectIntentRequest,
                             session: String)

// for response
case class ResponsePayload(categories: Seq[Category])

case class DialogflowResponse(fulfillmentText: String, payload: ResponsePayload, source: String)
