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
  extends SwaggerController with IntentOperation {
  override implicit protected val swagger = s

  private implicit val ec = system.dispatcher

  postWithDoc("/intents/?")(saveIntentOp) { request: Intent =>
    repository.saveAssign(request.isUser, request.date, request.eventName, request.id).map(Affected)
  }

  deleteWithDoc("/intents/?")(saveIntentOp) { request: Intent =>
    repository.removeAssign(request.isUser, request.date, request.eventName, request.id).map(Affected)
  }

  getWithDoc("/intents/?")(getIntentOp) { request: AssigneeRequest =>
    repository.findAssign(request.date, request.name)
  }
}

case class AssigneeRequest(@QueryParam date: DateTime,
                           @QueryParam name: String)

case class Affected(count: Int)

case class Intent(isUser: Boolean, date: DateTime, eventName: String, id: String)