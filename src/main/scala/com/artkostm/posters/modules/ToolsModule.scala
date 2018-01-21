package com.artkostm.posters.modules

import javax.inject.Singleton

import akka.actor.ActorSystem
import com.artkostm.posters.collector.EventsCollector
import com.artkostm.posters.repository.PostgresPostersRepository
import com.artkostm.posters.scraper.EventsScraper
import com.google.inject.{Module, Provides}
import com.twitter.inject.TwitterModule
import org.joda.time.DateTime

object ToolsModule extends TwitterModule {
  override def modules: Seq[Module] = Seq(ConfigModule, AkkaModule, DbModule)

  @Singleton @Provides def scraper(scraperConfig: ScraperConfig): EventsScraper = new EventsScraper(scraperConfig)

  @Singleton @Provides def collector(scraper: EventsScraper, repo: PostgresPostersRepository, system: ActorSystem): EventsCollector =
    new EventsCollector(scraper, (-2 to 30).map(DateTime.now().plusDays).map(_.withTimeAtStartOfDay()), repo, system)
}
