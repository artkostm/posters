package com.artkostm.posters.repository

import com.twitter.inject.{Injector, TwitterModule}

object DbModule extends TwitterModule {
  override def singletonStartup(injector: Injector): Unit = {
    PostgresEventsRepository.setUp
  }

  override def singletonShutdown(injector: Injector): Unit = {
    PostgresEventsRepository.db.close()
  }
}
