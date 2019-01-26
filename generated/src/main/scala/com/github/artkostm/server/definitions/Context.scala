package com.github.artkostm.server.definitions
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
case class Context(name: String, parameters: ContextParams, lifespan: Int)
object Context {
  implicit val encodeContext = {
    val readOnlyKeys = Set[String]()
    Encoder.forProduct3("name", "parameters", "lifespan") { (o: Context) => (o.name, o.parameters, o.lifespan) }.mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
  }
  implicit val decodeContext = Decoder.forProduct3("name", "parameters", "lifespan")(Context.apply _)
}