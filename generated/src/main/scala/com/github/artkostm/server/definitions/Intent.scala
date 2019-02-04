package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Intent(name: String, displayName: String)
object Intent {
  implicit val encodeIntent = {
    val readOnlyKeys = Set[String]()
    Encoder
      .forProduct2("name", "displayName") { (o: Intent) =>
        (o.name, o.displayName)
      }
      .mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeIntent = Decoder.forProduct2("name", "displayName")(Intent.apply _)
}
