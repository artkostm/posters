package com.artkostm.posters.worker

import java.sql.Timestamp

import akka.dispatch.ExecutionContexts
import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.hikari._
import org.joda.time.DateTime
import doobie.postgres._
import doobie.postgres.implicits._

import scala.concurrent.ExecutionContext

object Sql extends App {

  val transactor = HikariTransactor.newHikariTransactor[IO](driverClassName = "org.postgresql.Driver",
                                                            url = "jdbc:postgresql://localhost:5432/postgres",
                                                            user = "test",
                                                            pass = "12345")

  val date      = new Timestamp(DateTime.now().toDate.getTime)
  val eventName = "ko ko"
  val vids      = Array("1", "3", "4")
  val uids      = Array("5", "6", "7")

  final case class Vis(date: Timestamp, event_name: String, vids: Array[String], uids: Array[String])

  val insert =
    sql"""insert into visitors (date, event_name, vids, uids) values ($date, $eventName, $vids, $uids)""".update
      .withUniqueGeneratedKeys[Vis]("date", "event_name", "vids", "uids")

  (for {
    xa <- transactor
  } yield {
    val y = xa.yolo
    import y._
    val vis = insert.transact(xa).unsafeRunSync()
    println(vis)
  }).unsafeRunSync()
}
