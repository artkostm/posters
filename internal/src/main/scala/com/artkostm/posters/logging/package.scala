package com.artkostm.posters

import cats.effect.Sync
import doobie.util.log.{ExecFailure, LogHandler, ProcessingFailure, Success}
import org.log4s.getLogger
import org.log4s.MDC

package object logging {
  def mdcF[F[_]: Sync](kvs: (String, String)*)(logAction: => Unit): F[Unit] =
    Sync[F].delay(MDC.withCtx(kvs.map(t => (t._1, s"${t._1}=${t._2}")): _*)(logAction))

  val doobieLogHandler: LogHandler = {
    val logger = getLogger("doobie_logger")
    LogHandler {

      case Success(s, a, e1, e2) =>
        logger.info(s"""Successful Statement Execution:
                          |
                          |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                          |
                          | arguments = [${a.mkString(", ")}]
                          |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (${(e1 + e2).toMillis} ms total)
          """.stripMargin)

      case ProcessingFailure(s, a, e1, e2, t) =>
        logger.error(s"""Failed Resultset Processing:
                            |
                            |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                            |
                            | arguments = [${a.mkString(", ")}]
                            |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (failed) (${(e1 + e2).toMillis} ms total)
                            |   failure = ${t.getMessage}
          """.stripMargin)

      case ExecFailure(s, a, e1, t) =>
        logger.error(s"""Failed Statement Execution:
                            |
                            |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                            |
                            | arguments = [${a.mkString(", ")}]
                            |   elapsed = ${e1.toMillis} ms exec (failed)
                            |   failure = ${t.getMessage}
          """.stripMargin)
    }
  }
}
