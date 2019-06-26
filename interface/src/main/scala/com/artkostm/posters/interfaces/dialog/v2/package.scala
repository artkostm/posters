package com.artkostm.posters.interfaces.dialog

import java.time.LocalDate

package object v2 {
  // for request
  final case class Period(endDate: LocalDate, startDate: LocalDate) extends DateRange

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
  final case class DialogflowResponse(fulfillmentText: String, payload: ResponsePayload, source: String)
}
