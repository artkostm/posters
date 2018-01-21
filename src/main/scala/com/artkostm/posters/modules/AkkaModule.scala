package com.artkostm.posters.modules

import javax.inject.Singleton

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import com.google.inject.Provides
import com.twitter.inject.{Injector, TwitterModule}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object AkkaModule extends TwitterModule {
  private var actor: ActorRef = _

  @Singleton @Provides def actorSystem() = null

  override def singletonStartup(injector: Injector): Unit = {
    actor = injector.instance[ActorSystem].actorOf(Props[ShutdownActor])
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      val terminate: Future[Terminated] = injector.instance[ActorSystem].terminate()
      Await.result(terminate, Duration("10 seconds"))
    }))
  }

  override def singletonShutdown(injector: Injector): Unit = {
    actor ! Stop
  }
}

private[modules] class ShutdownActor extends Actor {
  override def receive: Receive = {
    case Stop =>
      implicit val executionContext: ExecutionContext = context.system.dispatcher
      context.system.scheduler.scheduleOnce(Duration.Zero)(System.exit(1))
  }
}