package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class ResponsePayload(categories: IndexedSeq[Category] = IndexedSeq.empty)
object ResponsePayload {
  implicit val encodeResponsePayload = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct1("categories") { (o: ResponsePayload) => o.categories }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeResponsePayload = Decoder.forProduct1("categories")(ResponsePayload.apply _)
}