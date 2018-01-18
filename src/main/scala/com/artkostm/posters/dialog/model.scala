package com.artkostm.posters.dialog

import org.joda.time.DateTime

case class Datetime(date: Option[DateTime], period: Option[String])

case class Parameters(category: List[String], datetime: Datetime)

case class ContextParams(datetime: String, `datetime.original`: String, category: List[String], `category.original`: String)

case class Context(name: String, parameters: ContextParams, lifespan: Int)

case class Metadata(intentId: String, webhookUsed: String, webhookForSlotFillingUsed: String, intentName: String)

case class Message(`type`: Int, speech: String)

case class Fulfillment(speech: String, messages: List[Message])

case class Result(source: String, resolvedQuery: String, speech: String, action: String, actionIncomplete: Boolean,
                  parameters: Parameters, contexts: List[Context], metadata: Metadata, fulfillment: Fulfillment, score: Double)

case class Status(code: Int, errorType: String, webhookTimedOut: Boolean)

case class DialogflowRequest(id: String, lang: String, result: Result, status: Status, sessionId: String)
