package com.artkostm.posters.controllers

import akka.actor.ActorSystem
import com.artkostm.posters.repository.PostgresPostersRepository
import com.google.inject.Inject
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finatra.request.QueryParam
import io.swagger.models.Swagger

class EventInfoController @Inject() (s: Swagger, repository: PostgresPostersRepository,
                                     system: ActorSystem)
  extends SwaggerController with EventInfoByLinkOperation {
  override implicit protected val swagger = s

  private implicit val ec = system.dispatcher

  getWithDoc("/info/?")(eventInfoByLinkOp) { request: EventInfoRequest =>
    repository.findInfo(request.link)
  }
}

case class EventInfoRequest(@QueryParam link: String)
