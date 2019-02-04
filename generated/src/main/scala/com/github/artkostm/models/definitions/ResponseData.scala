package com.github.artkostm.models.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class ResponseData(categories: IndexedSeq[Category] = IndexedSeq.empty)
object ResponseData {
  implicit val encodeResponseData = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct1("categories") { (o: ResponseData) =>
        o.categories
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeResponseData = Decoder.forProduct1("categories")(ResponseData.apply _)
}
