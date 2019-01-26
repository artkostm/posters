package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class QueryResult(queryText: String,
                       parameters: Parameters,
                       allRequiredParamsPresent: Boolean,
                       intent: Intent,
                       intentDetectionConfidence: Double,
                       diagnosticInfo: io.circe.Json,
                       languageCode: String)
object QueryResult {
  implicit val encodeQueryResult = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct7("queryText",
                   "parameters",
                   "allRequiredParamsPresent",
                   "intent",
                   "intentDetectionConfidence",
                   "diagnosticInfo",
                   "languageCode") { (o: QueryResult) =>
        (o.queryText,
         o.parameters,
         o.allRequiredParamsPresent,
         o.intent,
         o.intentDetectionConfidence,
         o.diagnosticInfo,
         o.languageCode)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeQueryResult = Decoder.forProduct7("queryText",
                                                       "parameters",
                                                       "allRequiredParamsPresent",
                                                       "intent",
                                                       "intentDetectionConfidence",
                                                       "diagnosticInfo",
                                                       "languageCode")(QueryResult.apply _)
}
