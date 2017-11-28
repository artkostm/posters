package com.artkostm.posters.modules

import com.artkostm.posters._
import com.artkostm.posters.repository.PostgresEventsRepository
import com.twitter.inject.{Injector, TwitterModule}

object DbModule extends TwitterModule {
  implicit val ec = actorSystem.dispatcher

  override def singletonStartup(injector: Injector): Unit = {
    PostgresEventsRepository.setUp
  }

  override def singletonShutdown(injector: Injector): Unit = {
    PostgresEventsRepository.db.close
  }
}
