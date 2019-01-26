package com.github.artkostm.models.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Result(source: String, resolvedQuery: String, speech: Option[String] = None, action: String, actionIncomplete: Boolean, parameters: Parameters, contexts: IndexedSeq[Context] = IndexedSeq.empty, metadata: Metadata, fulfillment: Fulfillment, score: Double)
object Result {
  implicit val encodeResult = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct10("source", "resolvedQuery", "speech", "action", "actionIncomplete", "parameters", "contexts", "metadata", "fulfillment", "score") { (o: Result) => (o.source, o.resolvedQuery, o.speech, o.action, o.actionIncomplete, o.parameters, o.contexts, o.metadata, o.fulfillment, o.score) }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeResult = Decoder.forProduct10("source", "resolvedQuery", "speech", "action", "actionIncomplete", "parameters", "contexts", "metadata", "fulfillment", "score")(Result.apply _)
}