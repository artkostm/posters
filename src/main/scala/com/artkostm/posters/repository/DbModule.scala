package com.artkostm.posters.repository

import com.twitter.inject.{Injector, TwitterModule}

object DbModule extends TwitterModule {
  override def singletonStartup(injector: Injector): Unit = {
    // initialize JVM-wide resources
  }

  override def singletonShutdown(injector: Injector): Unit = {
    // shutdown JVM-wide resources
  }
}
