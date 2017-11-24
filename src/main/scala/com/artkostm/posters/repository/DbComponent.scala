package com.artkostm.posters.repository

import slick.jdbc.JdbcProfile

trait DbComponent {
  val driver: JdbcProfile

  import driver.api._

  val db: Database
}
