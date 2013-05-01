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

import net.liftweb.common.Logger
import com.codeandmagic.ukgist.model.{PoliceCrimeData, Area}

/**
 * User: cvrabie
 * Date: 30/04/2013
 */
object PoliceCrimeImport extends App{
  override def main(args: Array[String]) {
    super.main(args)
  }
}

trait PoliceCrimeToolComponent{

  class PoliceCrimeTool(override val args:String*) extends Tool(args:_*) with Logger{
    val REQUIRED_PARAMETERS = 1

    if (args.contains("--one") && args.contains("--many"))
      throw new IllegalArgumentException("Do you want --one or --many?")
    val ONE = args.contains("--one")
    val CLEAR = args.contains("--clear")

    val HELP_MESSAGE =
      """
        |Imports one or multiple csv files as PoliceCrimeData in the database.
        |Usage: %s [--one|--many] [--clear] FOLDER/FILE
        |--one: Imports just one file as opposed to many of them.
        |--many (default): Recursively imports an entire folder of kml files.
        |--clear: Clears all the areas from the database before importing. Only the areas of the specified --source are removed.
      """.stripMargin.format(PROGRAM_NAME)




    override def apply():PoliceCrimeTool = {
      super.apply()
      this
    }

    def clear() = null

    def readOne(): PoliceCrimeData = null

    def readMany(): Seq[PoliceCrimeData] = null

    def writeAll(data: Seq[PoliceCrimeData]):Seq[PoliceCrimeData] = null

    def execute() {
      if(CLEAR) clear()
      val data = if(ONE) readOne()::Nil else readMany()
      info("Imported %d PoliceCrimeData".format(data.length))
      val saved = writeAll(data)
      info("Wrote %d areas to database".format(saved.length))
    }
  }

}
