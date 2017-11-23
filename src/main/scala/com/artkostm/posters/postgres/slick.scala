package com.artkostm.posters.postgres

import java.util.Date

import com.artkostm.posters._
import com.artkostm.posters.model.Events
import slick.dbio.DBIOAction
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object slicK extends App {

  val db = Database.forConfig("h2mem1")
  try {
    val date = new Date()
    val f = db.run(DBIO.seq(
      EventPersister.setupAction,
      EventPersister.get.result.map(r => println(s"After SetUp: $r")),
      EventPersister.saveAction("Кино", date, "Stranger things", "3,5"),
      EventPersister.get.result.map(r => println(s"After Create: $r")),
      EventPersister.saveAction("Кино", date, "Stranger things", "3,5,6"),
      EventPersister.get.result.map(r => println(s"After Update: $r")),
      EventPersister.saveAction("Цирк", new Date, "Медведи", "1"),
      EventPersister.get.result.map(r => println(s"After Create: $r")),
      EventPersister.events.delete,
      EventPersister.get.result.map(r => println(s"After Delete: $r")),
      EventPersister.events.schema.drop
    ))

    Await.result(f, Duration.Inf)

  } finally db.close()
}
