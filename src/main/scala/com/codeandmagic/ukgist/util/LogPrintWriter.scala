package com.codeandmagic.ukgist.util

import java.io.{Writer, PrintWriter}
import net.liftweb.common.{Logger, Loggable}

/**
 * User: cvrabie
 * Date: 03/08/2012
 */

class LogPrintWriter(name:Class[_], level:LogLevel.Value) extends PrintWriter(new LogWriter(name, level))

object LogLevel extends Enumeration{
  val ERROR, WARN, INFO, DEBUG, TRACE = Value
}

class LogWriter(val name:Class[_], val level:LogLevel.Value) extends Writer{
  val buf = new StringBuilder
  val logger = Logger(name)

  def write(p1: Array[Char], offset: Int, len: Int) {
    buf.appendAll(p1, offset, len)
  }

  def flush() {
    val msg = buf.toString()
    level match {
      case LogLevel.ERROR => logger.error(msg)
      case LogLevel.WARN => logger.warn(msg)
      case LogLevel.INFO => logger.info(msg)
      case LogLevel.DEBUG => logger.debug(msg)
      case LogLevel.TRACE => logger.trace(msg)
    }
    buf.clear()
  }

  def close() {
    buf.clear()
  }
}
