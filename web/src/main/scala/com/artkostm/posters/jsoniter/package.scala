package com.artkostm.posters

import com.artkostm.posters.interfaces.event.EventInfo
import com.artkostm.posters.interfaces.schedule.Category
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}

package object jsoniter extends JsoniterInstances {
  object ValueCodecs {
    implicit val eventInfoCodec: JsonValueCodec[EventInfo] = JsonCodecMaker.make[EventInfo](CodecMakerConfig())
    implicit val categoryCodec: JsonValueCodec[Category] = JsonCodecMaker.make[Category](CodecMakerConfig())
  }
}
