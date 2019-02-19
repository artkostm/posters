package com.artkostm.posters.algebra

import java.time.Instant

import com.artkostm.posters.interfaces.intent.Intent

trait VisitorStore[F[_]] {
  def deleteOld(today: Instant): F[Int]
  def save(intent: Intent): F[Intent]
  def find(date: Instant, eventName: String): F[Option[Intent]]
  def asVolunteer(id: String, intent: Intent): F[Intent]
  def asPlainUser(id: String, intent: Intent): F[Intent]
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