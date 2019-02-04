package com.artkostm.posters

import java.nio.charset.StandardCharsets

import org.postgresql.util.PGobject
import com.github.plokhotnyuk.jsoniter_scala.core._

package object doobiemeta {
  implicit def jsonbMeta[A: Manifest: JsonValueCodec]: doobie.Meta[A] =
    doobie.Meta
      .other[PGobject]("jsonb")
      .xmap[A](
        pgObject => readFromArray(pgObject.getValue.getBytes(StandardCharsets.UTF_8)),
        jsonObject => {
          val pgObject = new PGobject()
          pgObject.setType("jsonb")
          pgObject.setValue(new String(writeToArray(jsonObject), StandardCharsets.UTF_8))
          pgObject
        }
      )
}
