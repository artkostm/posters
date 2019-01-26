package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Parameters(category: IndexedSeq[String] = IndexedSeq.empty, datetime: Datetime)
object Parameters {
  implicit val encodeParameters = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct2("category", "datetime") { (o: Parameters) =>
        (o.category, o.datetime)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeParameters = Decoder.forProduct2("category", "datetime")(Parameters.apply _)
}
