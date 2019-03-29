package com.artkostm.posters

import com.artkostm.posters.interfaces.event.{EventData, EventInfo}
import com.artkostm.posters.interfaces.intent.{Intent, Intents}
import com.artkostm.posters.interfaces.schedule.{Category, Day}
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}

package object jsoniter extends JsoniterInstances {
  object codecs {
    implicit val eventInfoCodec: JsonValueCodec[EventInfo] = JsonCodecMaker.make[EventInfo](CodecMakerConfig())
    implicit val categoryCodec: JsonValueCodec[Category]   = JsonCodecMaker.make[Category](CodecMakerConfig())
    implicit val dayCodec: JsonValueCodec[Day]             = JsonCodecMaker.make[Day](CodecMakerConfig())
    implicit val intentsCodec: JsonValueCodec[Intents]     = JsonCodecMaker.make[Intents](CodecMakerConfig())
    implicit val intentCodec: JsonValueCodec[Intent]       = JsonCodecMaker.make[Intent](CodecMakerConfig())
    implicit val eventDataCodec: JsonValueCodec[EventData] = JsonCodecMaker.make[EventData](CodecMakerConfig())
    implicit val categoriesCodec: JsonValueCodec[List[Category]] =
      JsonCodecMaker.make[List[Category]](CodecMakerConfig())
  }
}
