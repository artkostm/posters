package com.artkostm.posters.controllers

import akka.actor.ActorSystem
import com.artkostm.posters.model.Assign
import com.artkostm.posters.repository.PostgresPostersRepository
import com.google.inject.Inject
import com.jakehschwartz.finatra.swagger.SwaggerController
import com.twitter.finatra.request.QueryParam
import io.swagger.models.Swagger
import org.joda.time.DateTime

class IntentsController @Inject() (s: Swagger, repository: PostgresPostersRepository,
                                   system: ActorSystem)
  extends SwaggerController with GetIntentOperation with SaveIntentOperation {
  override implicit protected val swagger = s

  private implicit val ec = system.dispatcher

  postWithDoc("/intents/?")(saveIntentOp) { request: Assign =>
    repository.save(request)
  }

  getWithDoc("/intents/?")(getIntentOp) { request: AssigneeRequest =>
    repository.find(request.category, request.date, request.name)
  }
}

case class AssigneeRequest(@QueryParam date: DateTime,
                           @QueryParam category: String,
                           @QueryParam name: String)
