package com.artkostm.posters.interfaces.dialog

import java.time.LocalDate

package object v1 {
  final case class Period(endDate: LocalDate, startDate: LocalDate) extends DateRange

  final case class Datetime(date: Option[LocalDate], period: Option[Period])

  final case class Parameters(category: List[String], datetime: Datetime)

  final case class ContextParams(datetime: String,
                                 `datetime.original`: String,
                                 category: List[String],
                                 `category.original`: String)

  final case class Context(name: String, parameters: ContextParams, lifespan: Int)

  final case class Metadata(intentId: String, webhookUsed: String, webhookForSlotFillingUsed: String, intentName: String)

  final case class Message(`type`: Int, speech: String)

  final case class Fulfillment(speech: String, messages: List[Message])

  final case class Result(source: String,
                          resolvedQuery: String,
                          speech: Option[String],
                          action: String,
                          actionIncomplete: Boolean,
                          parameters: Parameters,
                          contexts: List[Context],
                          metadata: Metadata,
                          fulfillment: Fulfillment,
                          score: Double)

  final case class Status(code: Int, errorType: String, webhookTimedOut: Option[Boolean])

  final case class DialogflowRequest(id: String, lang: String, result: Result, status: Status, sessionId: String)

  final case class DialogflowResponse(speech: String, data: ResponsePayload, source: String)
}
