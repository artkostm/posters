package com.artkostm.posters

import java.time.LocalDate

import com.artkostm.posters.interfaces.event.{EventData, EventInfo}
import com.artkostm.posters.interfaces.dialog.v1.{
  Period,
  DialogflowRequest => Request1,
  DialogflowResponse => Response1
}
import com.artkostm.posters.interfaces.dialog.v2.{DialogflowRequest => Request2, DialogflowResponse => Response2}
import com.artkostm.posters.interfaces.intent.{Intent, Intents}
import com.artkostm.posters.interfaces.schedule.{Category, Day}
import com.github.plokhotnyuk.jsoniter_scala.core.{JsonReader, JsonValueCodec, JsonWriter}
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

    implicit val periodV1JsonValueCodec = new JsonValueCodec[Period] {
      override def decodeValue(in: JsonReader, default: Period): Period =
        in.readString("").split("/") match {
          case Array(start, end) =>
            if (start > end)
              in.readNullOrError(null, "Incorrect Period format, start of period can't be after end of period")
            else Period(LocalDate.parse(start), LocalDate.parse(end))
          case _ => in.readNullOrError(null, "Incorrect Period format, should be yyyy-MM-dd/yyyy-MM-dd")
        }

      override def encodeValue(x: Period, out: JsonWriter): Unit =
        out.writeVal(s"${x.startDate}/${x.endDate}")

      override def nullValue: Period = null
    }

    implicit val dfRequest1Codec: JsonValueCodec[Request1]   = JsonCodecMaker.make[Request1](CodecMakerConfig())
    implicit val dfResponse1Codec: JsonValueCodec[Response1] = JsonCodecMaker.make[Response1](CodecMakerConfig())
    implicit val dfRequest2Codec: JsonValueCodec[Request2]   = JsonCodecMaker.make[Request2](CodecMakerConfig())
    implicit val dfResponse2Codec: JsonValueCodec[Response2] = JsonCodecMaker.make[Response2](CodecMakerConfig())
  }
}
