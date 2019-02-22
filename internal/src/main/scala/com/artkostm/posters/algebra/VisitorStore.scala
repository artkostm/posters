package com.artkostm.posters.algebra

import java.time.Instant

import com.artkostm.posters.interfaces.intent.Intents

trait VisitorStore[F[_]] {
  def deleteOld(today: Instant): F[Int]
  def save(intent: Intents): F[Intents]
  def find(date: Instant, eventName: String): F[Option[Intents]]
  def asVolunteer(id: String, intent: Intents): F[Intents]
  def asPlainUser(id: String, intent: Intents): F[Intents]
}

//object testJson extends App {
//  println(Instant.now)
//  import com.github.plokhotnyuk.jsoniter_scala.core._
//
//  implicit val intentCodec: JsonValueCodec[Intent] = JsonCodecMaker.make[Intent](CodecMakerConfig())
//
//  val intent: Intent = readFromArray(
//    """
//      |{"date":"2019-02-19T13:56:24.678Z", "event_name":"event name"}
//    """.stripMargin.getBytes)
//
//  println(intent)
//
//  println(new String(writeToArray(Intent(Instant.now, "event name"))))
//}
