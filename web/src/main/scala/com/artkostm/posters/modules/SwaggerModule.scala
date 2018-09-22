package com.artkostm.posters.modules

import javax.inject.Singleton
import com.google.inject.{Module, Provides}
import com.jakehschwartz.finatra.swagger.SwaggerModule
import io.swagger.models.auth.BasicAuthDefinition
import io.swagger.models.{Info, Swagger}

object PostersSwaggerModule extends SwaggerModule {
  override def modules: Seq[Module] = Seq(ConfigModule)

  @Singleton @Provides def swagger(appConfig: AppConfig): Swagger = {
    val swagger = new Swagger()

    val info = new Info()
      .description("The Categories / Events retrieve API")
      .version(appConfig.version)
      .title("Posters / Events Retrieve API")

    swagger
      .info(info)
      .addSecurityDefinition("sampleBasic", {
        val d = new BasicAuthDefinition()
        d.setType("basic")
        d
      })

    swagger
  }
}
