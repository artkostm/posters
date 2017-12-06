package com.artkostm.posters.modules

import com.artkostm.posters._
import com.artkostm.posters.repository.PostgresPostersRepository
import com.twitter.inject.{Injector, TwitterModule}

object DbModule extends TwitterModule {
  implicit val ec = actorSystem.dispatcher

  override def singletonStartup(injector: Injector): Unit = {
    PostgresPostersRepository.setUp
  }

  override def singletonShutdown(injector: Injector): Unit = {
    PostgresPostersRepository.db.close
  }
}
