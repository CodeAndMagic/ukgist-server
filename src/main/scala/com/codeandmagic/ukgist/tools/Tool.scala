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

package com.codeandmagic.ukgist.tools

import java.io.{InputStream, File, PrintStream}
import com.codeandmagic.ukgist.util.FileOps._

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
abstract class Tool(val args:String*){
  val OUT:PrintStream = System.out
  val IN:InputStream = System.in

  val argsWithIndex = args.zipWithIndex
  private val name = getClass.getSimpleName
  val PROGRAM_NAME = name.substring(0,name.indexOf("$") match {
    case i if i>0 => i
    case _ => name.length
  })


  val REQUIRED_PARAMETERS:Int
  def isArgumentParameter(i:Int) = i < args.length-REQUIRED_PARAMETERS-1 && !args(i+1).startsWith("--")

  def getArgumentParameter[T](ARG_NAME:String,deserializer:(String)=>T,default:T, extraMsg:String):T =
    argsWithIndex.find(_ match {
      case (ARG_NAME,i) if isArgumentParameter(i) => true
      case (ARG_NAME,_) => throw new IllegalArgumentException("You need to specify a %s. %s".format(ARG_NAME,extraMsg))
      case _ => false
    }) match {
      case Some((_,i)) => deserializer(args(i+1))
      case None => default
    }

  val defaultStringDeserializer = (s:String) => s

  val fileDeserializer = (path:String) => new File(path) match {
    case f if f.exists && f.isFile => f
    case _ => throw new IllegalArgumentException("%s is not a valid file.".format(path))
  }

  def fileWithTypeDeserializer(extension:String) = (path:String) => fileDeserializer(path) match {
    case f if f.extension==extension => f
    case _ => throw new IllegalArgumentException("%s is not of expected type *.'%s'".format(path,extension))
  }

  val folderDeserializer = (path:String) => new File(path) match {
    case f if f.exists && f.isDirectory => f
    case _ => throw new IllegalArgumentException("%s is not a valid folder".format(path))
  }

  def apply():Tool = {
    if (args.size == 0 || (args.size == 1 && args(0) == "--help")) help()
    else execute()
    this
  }

  val INTERVAL_FORMAT_MESSAGE =
    """
      |You can specify an interval like this:
      |D1/D2 => interval start on day D1 and ends just before D2
      |/D2 => interval has no start and ends just before D2
      |D1/ => interval starts on D1 and has no end
      |D1 => interval depends on the format of D1 (see below)
      |D1 and D2 are dates specified in either yyyy-MM-dd, yyyy-MM or yyyy formats
      |If for example you specify an interval of 2012 this means the start is on
      |2012-01-01T00:00:00 and the end is on 2013-01-01T00:00:00 (exclusive).
      |If you use 2012-10-24/ this will define an interval starting on
      |2012-10-24T00:00:00 and with no ending.
    """.stripMargin

  val HELP_MESSAGE:String
  def help() = OUT.println(HELP_MESSAGE)
  def execute()
}
