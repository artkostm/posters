package com.artkostm.posters

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.W

object Main extends App {
  type ApiKey = String Refined MatchesRegex[W.`"[a-zA-Z0-9]{25,40}"`.T]

  import ciris.{env, prop}

  import ciris.refined._

  println(env[ApiKey]("API_KEY").value match {
    case Right(key) => s"Here is the key: $key"
    case _ => "Something went wrong"
  })
}//extends PostersServer
