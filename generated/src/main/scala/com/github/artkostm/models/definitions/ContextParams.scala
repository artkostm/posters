package com.github.artkostm.models.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class ContextParams(datetime: String, category: IndexedSeq[String] = IndexedSeq.empty, datetimeOriginal: String, categoryOriginal: String)
object ContextParams {
  implicit val encodeContextParams = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct4("datetime", "category", "datetime.original", "category.original") { (o: ContextParams) => (o.datetime, o.category, o.datetimeOriginal, o.categoryOriginal) }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeContextParams = Decoder.forProduct4("datetime", "category", "datetime.original", "category.original")(ContextParams.apply _)
}