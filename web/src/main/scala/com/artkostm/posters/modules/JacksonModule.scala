package com.artkostm.posters.modules

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.twitter.finatra.json.modules.FinatraJacksonModule

object PostersJacksonModule extends FinatraJacksonModule {
  override protected val propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
}
