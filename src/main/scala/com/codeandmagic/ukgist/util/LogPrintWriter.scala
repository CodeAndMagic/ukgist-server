/*
 Copyright 2013 Cristian Vrabie, Evelina Vrabie

 This file is part of UKGist.

 UKGist is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 UKGist is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with UKGist.  If not, see <http://www.gnu.org/licenses/>.
 */

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
