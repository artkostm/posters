package com.artkostm.posters.interfaces.dialog.v2

import java.time.LocalDate

import com.artkostm.posters.interfaces.schedule.Category

// for request
final case class Period(endDate: LocalDate, startDate: LocalDate)

final case class Datetime(date: Option[LocalDate], period: Option[Period])

final case class Parameters(category: List[String], datetime: Datetime)

final case class Intent(name: String, displayName: String)

final case class DiagnosticInfo()

final case class QueryResult(queryText: String,
                       parameters: Parameters,
                       allRequiredParamsPresent: Boolean,
                       intent: Intent,
                       intentDetectionConfidence: Double,
                       diagnosticInfo: DiagnosticInfo,
                       languageCode: String)

final case class Payload()

final case class OriginalDetectIntentRequest(payload: Payload)

final case class DialogflowRequest(responseId: String,
                             queryResult: QueryResult,
                             originalDetectIntentRequest: OriginalDetectIntentRequest,
                             session: String)

// for response
final case class ResponsePayload(categories: Seq[Category])

final case class DialogflowResponse(fulfillmentText: String, payload: ResponsePayload, source: String)
