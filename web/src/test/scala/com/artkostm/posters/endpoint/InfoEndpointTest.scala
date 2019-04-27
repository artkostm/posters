package com.artkostm.posters.endpoint

import cats.Foldable
import cats.effect.IO
import com.artkostm.posters.algebra.InfoStore
import com.artkostm.posters.endpoint.error.HttpErrorHandler
import com.artkostm.posters.interfaces.auth.User
import com.artkostm.posters.interfaces.event.{EventData, EventInfo}
import org.http4s.{AuthedRequest, Method, Request, Uri}
import org.http4s.implicits._
import org.scalatest.FlatSpec

class InfoEndpointTest extends FlatSpec {

  implicit val errorHandler = new HttpErrorHandler[IO]

  val infoRepo = new InfoStore[IO] {
    override def deleteOld(): IO[Int] = IO.pure(1)

    override def save(info: EventInfo): IO[EventInfo] = IO.pure(info)

    override def save[K[_] : Foldable](info: K[(String, EventData, EventData)]): IO[Int] = IO.pure(1)

    override def find(link: String): IO[Option[EventInfo]] = IO.pure(Some(EventInfo(link, EventData("description", List(), List()))))
  }

  "Endpoint" should "return event info by link" in {
    val infoEndpoint = new InfoEndpoint[IO](infoRepo)

    val link = "https://test.io/img.jpg"

    val responseIO = infoEndpoint.endpoints.orNotFound(AuthedRequest(User("apiKey", "role", "id"), Request[IO](Method.GET, Uri.unsafeFromString(s"/v1/events?link=$link"))))

    val response = responseIO.unsafeRunSync()

    println(new String(response.body.compile.toList.unsafeRunSync().toArray))
  }

}
