package com.artkostm.posters

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.finatra.http.routing.HttpRouter

class PostersServer extends HttpServer {
  override protected def configureHttp(router: HttpRouter): Unit =
    router.add(new HelloController)
}

class HelloController extends Controller {
  get("/hello") { request: Request =>
    "Hello From Anmol. Let's Learn Finatra"
  }
}

object Server extends PostersServer