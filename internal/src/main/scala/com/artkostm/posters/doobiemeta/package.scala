package com.artkostm.posters

import java.nio.charset.StandardCharsets

import org.postgresql.util.PGobject
import com.github.plokhotnyuk.jsoniter_scala.core._
import doobie.Meta.Advanced

import scala.reflect.runtime.universe.TypeTag

package object doobiemeta {
  implicit def jsonbMeta[A: JsonValueCodec: TypeTag]: doobie.Meta[A] =
    Advanced
      .other[PGobject]("jsonb")
      .timap[A] { pgObject =>
        readFromArray(pgObject.getValue.getBytes(StandardCharsets.UTF_8))
      } { jsonObject =>
        val pgObject = new PGobject()
        pgObject.setType("jsonb")
        pgObject.setValue(new String(writeToArray(jsonObject), StandardCharsets.UTF_8))
        pgObject
      }
}
