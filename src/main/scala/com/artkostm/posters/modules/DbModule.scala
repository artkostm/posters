package com.artkostm.posters.modules

import com.artkostm.posters._
import com.artkostm.posters.repository.H2EventsRepository
import com.twitter.inject.{Injector, TwitterModule}

object DbModule extends TwitterModule {
  implicit val ec = actorSystem.dispatcher

  override def singletonStartup(injector: Injector): Unit = {
    H2EventsRepository.setUp
  }

  override def singletonShutdown(injector: Injector): Unit = {
    H2EventsRepository.db.close
  }
}
