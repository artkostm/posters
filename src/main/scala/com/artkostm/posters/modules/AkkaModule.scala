package com.artkostm.posters.modules

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorRef, Props, Terminated}
import com.artkostm.posters.actorSystem
import com.twitter.inject.{Injector, TwitterModule}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object AkkaModule extends TwitterModule {
  private val actor: ActorRef = actorSystem.actorOf(Props[ShutdownActor])

  override def singletonStartup(injector: Injector): Unit = Runtime.getRuntime.addShutdownHook(new Thread(() => {
      val terminate: Future[Terminated] = actorSystem.terminate()
      Await.result(terminate, Duration("10 seconds"))
    }
  ))

  override def singletonShutdown(injector: Injector): Unit = actor ! Stop
}

private[modules] class ShutdownActor extends Actor {
  override def receive: Receive = {
    case Stop =>
      implicit val executionContext: ExecutionContext = context.system.dispatcher
      context.system.scheduler.scheduleOnce(Duration.Zero)(System.exit(1))
  }
}