package com.artkostm.posters.repository

import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.actor.SupervisorStrategy.Stop
import com.twitter.inject.{Injector, TwitterModule}
import com.artkostm.posters._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

object DbModule extends TwitterModule {
  implicit val ec = actorSystem.dispatcher
  private val actor: ActorRef = actorSystem.actorOf(Props[ShutdownActor])

  override def singletonStartup(injector: Injector): Unit = {
    PostgresEventsRepository.setUp
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      val terminate: Future[Terminated] = actorSystem.terminate()
      Await.result(terminate, Duration("10 seconds"))
    }))
  }

  override def singletonShutdown(injector: Injector): Unit = {
    PostgresEventsRepository.db.close()
    actor ! Stop
  }
}

class ShutdownActor extends Actor {
  override def receive: Receive = {
    case Stop =>
      println("Stopping application")
      implicit val executionContext: ExecutionContext = context.system.dispatcher
      context.system.scheduler.scheduleOnce(Duration.Zero)(System.exit(1))
  }
}