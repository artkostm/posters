package com.artkostm.posters.controllers

import akka.actor.ActorSystem
import com.artkostm.posters.graphql.TestSchema
import com.artkostm.posters.model.{Category, Description, Event, Media}
import com.artkostm.posters.repository.PostgresPostersRepository
import com.google.inject.Inject
import com.twitter.finatra.http.Controller
import sangria.execution.Executor
import sangria.parser.{QueryParser, SyntaxError}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class GraphQlController @Inject() (repository: PostgresPostersRepository,
                                   system: ActorSystem) extends Controller {

  private implicit val ec = system.dispatcher

  post("/test") { request: GraphQlRequest =>
    QueryParser.parse(request.query) match {
      case Success(queryAst) => Executor.execute(TestSchema.instance, queryAst, new SimpleRepo, operationName = request.operationName)
        .map(x => response.ok(x.toString))
      case Failure(error: SyntaxError) => Future.successful(response.badRequest(error.getMessage()))
    }
  }
}

case class GraphQlRequest(query: String, operationName: Option[String])

class SimpleRepo {

  def categories(): List[Category] = List(
    Category("category1", List(
      Event(Media("link1", "img1"), "event name1", Description("desc1", Some("ticket1"), true))
    )),
    Category("category2", List(
      Event(Media("link2", "img2"), "event name2", Description("desc2", None, false))
    ))
  )
}