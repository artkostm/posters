package com.artkostm.posters.modules

import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.sql.DataSource

import akka.actor.ActorSystem
import com.artkostm.posters.repository.{PostersRepository, PostgresPostersRepository}
import com.google.inject.{Module, Provides}
import com.twitter.inject.{Injector, TwitterModule}
import com.typesafe.config.Config
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import scala.concurrent.duration.FiniteDuration

object DbModule extends TwitterModule {
  override protected[inject] def modules: Seq[Module] = Seq(ConfigModule, AkkaModule, ToolsModule)

  @Singleton @Provides def postgresDataSource(config: Config): DataSource = {
    val uri = new URI(config.getString("postgres.url"))
    val user = uri.getUserInfo().split(":")(0)
    val password = uri.getUserInfo().split(":")(1)
    val c = new HikariConfig()
    c.setJdbcUrl(s"jdbc:postgresql://${uri.getHost()}:${uri.getPort()}${uri.getPath()}?sslmode=require")
    c.setUsername(user)
    c.setPassword(password)
    c.setMaximumPoolSize(1)
    c.addDataSourceProperty("sslmode", "require")
    c.setConnectionTimeout(120000)
    new HikariDataSource(c)
  }

  @Singleton @Provides def postersRepository(ds: DataSource): PostgresPostersRepository = new PostersRepository(ds)

  override def singletonStartup(injector: Injector): Unit = {
    val system = injector.instance[ActorSystem]
    implicit val ec = system.dispatcher
    //injector.instance[PostgresPostersRepository].setUp
    system.scheduler.schedule(FiniteDuration(10, TimeUnit.SECONDS), FiniteDuration(24, TimeUnit.HOURS)) {
      //injector.instance[EventsCollector].run()
    }
  }

  override def singletonShutdown(injector: Injector): Unit = {
    implicit val ec = injector.instance[ActorSystem].dispatcher
    injector.instance[PostgresPostersRepository].db.close
  }
}
