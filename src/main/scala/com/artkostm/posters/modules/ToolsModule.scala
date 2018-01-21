package com.artkostm.posters.modules

import javax.inject.Singleton

import com.artkostm.posters.collector.EventsCollector
import com.artkostm.posters.repository.PostgresPostersRepository
import com.artkostm.posters.scraper.EventsScraper
import com.google.inject.{Module, Provides}
import com.twitter.inject.TwitterModule
import org.joda.time.DateTime

object ToolsModule extends TwitterModule {
  override protected[inject] def modules: Seq[Module] = Seq(ConfigModule, AkkaModule, DbModule)

  @Singleton @Provides def scraper(scraperConfig: ScraperConfig): EventsScraper = new EventsScraper(scraperConfig)

  @Singleton @Provides def collector(scraper: EventsScraper, repo: PostgresPostersRepository): EventsCollector =
    new EventsCollector(scraper, (-2 to 30).map(DateTime.now().plusDays).map(_.withTimeAtStartOfDay()), repo)
}
