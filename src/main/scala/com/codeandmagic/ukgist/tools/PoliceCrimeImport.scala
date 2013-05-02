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
import com.codeandmagic.ukgist.model.{Interval, Information, PoliceCrimeData}
import java.io.File
import scala.io.Source
import com.codeandmagic.ukgist.dao.PoliceAreaDaoComponent
import com.codeandmagic.ukgist.util.Dec

/**
 * User: cvrabie
 * Date: 30/04/2013
 */
object PoliceCrimeImport extends App{
  override def main(args: Array[String]) {
    super.main(args)
  }
}

trait PoliceCrimeImportToolComponent{
  this:PoliceAreaDaoComponent =>

  val policeCrimeImportTool:PoliceCrimeImportTool

  class PoliceCrimeImportTool(override val args:String*) extends Tool(args:_*) with Logger{
    val REQUIRED_PARAMETERS = 1

    if (args.contains("--one") && args.contains("--many"))
      throw new IllegalArgumentException("Do you want --one or --many?")
    val ONE = args.contains("--one")
    val CLEAR = args.contains("--clear")

    val VALIDITY_DESERIALIZER = (s:String) => Interval.unapply(s) match {
      case Some(interval) => interval
      case _ => throw new IllegalArgumentException("Incorrect interval")
    }

    val VALIDITY:Interval = getArgumentParameter("--valid",VALIDITY_DESERIALIZER,Interval.FOREVER,INTERVAL_FORMAT_MESSAGE)

    val CSV_EXTENSION = "csv"
    val CSV_FILE_PATH_DESERIALIZER = fileWithTypeDeserializer(CSV_EXTENSION)

    private[tools] val PATH_MISSING = "You need to specify a file or folder path as the last argument."
    val PATH:File = (ONE,args.length>0,args.lastOption) match {
      case (true,true,Some(path)) => CSV_FILE_PATH_DESERIALIZER(path)
      case (false,true,Some(path)) => folderDeserializer(path)
      case (_,true,None) => throw new IllegalArgumentException(PATH_MISSING)
      case (_,false,_) => null //fail silently since we're not going to use the path anyway
    }

    info("Fetching all police PoliceAreas")
    val AREAS = policeAreaDao.listAll()
    info("Found %d areas".format(AREAS.size))

    val HELP_MESSAGE =
      """
        |Imports one or multiple csv files as PoliceCrimeData in the database.
        |Usage: %s [--one|--many] [--clear] FOLDER/FILE
        |--one: Imports just one file as opposed to many of them.
        |--many (default): Recursively imports an entire folder of kml files.
        |--clear: Clears all the areas from the database before importing. Only the areas of the specified --source are removed.
        |--valid: The interval while this is valid. Specify in the format 'YYYYMMDD-YYYYMMDD'. Either interval margins can be omitted.
      """.stripMargin.format(PROGRAM_NAME)

    override def apply():PoliceCrimeImportTool = {
      super.apply()
      this
    }

    def clear() = null

    def readOne(): Seq[PoliceCrimeData] = readOne(PATH, Nil)

    protected def readOne(file:File, breadcrumb:Seq[String]):Seq[PoliceCrimeData] = {
      info("Reading %s".format(file.getAbsolutePath))
      val src = Source.fromFile(file)
      src.getLines().flatMap( readOne(_) ).toSeq
    }

    val CSV_HEADER = ("Month,Force,Neighbourhood,All crime,Anti-social behaviour,Burglary,Criminal damage and arson," +
      "Drugs,Other theft,Public disorder and weapons,Robbery,Shoplifting,Vehicle crime,Violent crime," +
      "Other crime").toLowerCase().split(",").map(_.trim).toIndexedSeq

    protected def readOne(line:String):Option[PoliceCrimeData] = line.toLowerCase.split(",").map(_.trim).toIndexedSeq match {

      case CSV_HEADER => {
        info("Read CSV header")
        None
      }

      case IndexedSeq(date, policeForce, policeNeighborhood, Dec(v1), Dec(v2), Dec(v3), Dec(v4), Dec(v5), Dec(v6), Dec(v7),
        Dec(v8), Dec(v9), Dec(v10), Dec(v11), Dec(v12)) => {

        info("Read CSV line matching the PoliceCrimeData format")
        AREAS.filter( a =>
          a.policeForce.toLowerCase == policeForce &&
          a.policeNeighborhood.toLowerCase == policeNeighborhood &&
          a.validity.isEnclosing(VALIDITY)
        ).headOption match {

          case Some(area) => {
            val info = new Information(id = -1, PoliceCrimeData.discriminator, area, VALIDITY)
            Some(new PoliceCrimeData(id = -1, information = info, allCrime = v1, antiSocialBehaviour = v2, burglary = v3,
            criminalDamage = v4, drugs = v5, otherTheft = v6, publicDisorder = v7, robbery = v8, shoplifting = v9,
            vehicleCrime = v10, violentCrime = v11, otherCrime = v12))
          }

          case _ => throw new IllegalArgumentException(("Could not find a matching PoliceArea for force:%s and " +
            "neighborhood %s").format(policeForce,policeNeighborhood))

        }
      }

      case _ => throw new IllegalArgumentException("Could not extract PoliceCrimeData from line %s".format(line))
    }

    def readMany(): Seq[PoliceCrimeData] = null

    def writeAll(data: Seq[PoliceCrimeData]):Seq[PoliceCrimeData] = null

    def execute() {
      if(CLEAR) clear()
      val data = if(ONE) readOne() else readMany()
      info("Imported %d PoliceCrimeData".format(data.length))
      val saved = writeAll(data)
      info("Wrote %d areas to database".format(saved.length))
    }
  }

}