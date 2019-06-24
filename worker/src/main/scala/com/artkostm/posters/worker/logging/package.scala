package com.artkostm.posters.worker

import cats.effect.Sync
import org.log4s.MDC

package object logging {
  def mdcF[F[_]: Sync](kvs: (String, String)*)(logAction: => Unit): F[Unit] =
    Sync[F].delay(MDC.withCtx(kvs.map(t => (t._1, s"${t._1}=${t._2}")): _*)(logAction))
}
