package com.artkostm.posters.repository

import com.artkostm.posters.model.Assign
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object slicK extends App {

  H2EventsRepository.setUp
  try {
    val date = DateTime.now
    val f = for {
      _ <- H2EventsRepository.all.map(list => println(s"Before saving: $list"))
      saved <- H2EventsRepository.save(Assign("Кино", date, "мстители", "6"))
      _ <- H2EventsRepository.all.map(list => println(s"After saving: $list ===> saved: $saved"))
      updated <- H2EventsRepository.save(Assign("Кино", date, "мстители", "6,7"))
      _ <- H2EventsRepository.all.map(list => println(s"After updating: $list ===> saved: $saved"))
    } yield ()

    Await.result(f, Duration.Inf)

  } finally H2EventsRepository.db.close()
}
