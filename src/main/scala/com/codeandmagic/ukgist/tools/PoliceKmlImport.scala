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

import com.codeandmagic.ukgist.model._
import com.codeandmagic.ukgist.util.FileOps._
import com.vividsolutions.jts.geom.{Polygon => JstPolygon}
import java.io.{FileFilter, File}
import net.liftweb.util.Helpers.tryo
import de.micromata.opengis.kml.v_2_2_0.Kml
import scala.Some
import net.liftweb.common.Logger
import com.codeandmagic.ukgist.dao.PoliceAreaDao
import com.codeandmagic.ukgist.ComponentRegistry

/**
 * User: cvrabie
 * Date: 26/03/2013
 */
object PoliceKmlImport extends App {
  override def main(args: Array[String]) {
    super.main(args)
    new PoliceKmlTool(args:_*).apply()
  }
}
/**
 * Tool that inserts into the database [[com.codeandmagic.ukgist.model.PolygonArea]]s based on the KML files
 * provided by the Police Data website http://www.police.uk/data
 */
class PoliceKmlTool(override val args:String*) extends Tool(args:_*) with Logger{
  val REQUIRED_PARAMETERS = 1

  if (args.contains("--one") && args.contains("--many"))
    throw new IllegalArgumentException("Do you want --one or --many?")
  val ONE = args.contains("--one")
  val CLEAR = args.contains("--clear")

  val SOURCE_DEFAULT = Area.Source.POLICE

  val SOURCE_CSV = "Possible values are %s".format(Area.Source.CSV)

  val areaSourceDesearializer = (s:String) => try{ Area.Source.withName(s) }
    catch { case e:NoSuchElementException => throw new NoSuchElementException("No suck Area.Source %s. %s".format(s,SOURCE_CSV)) }

  val SOURCE:Area.Source.Value = getArgumentParameter("--source", areaSourceDesearializer, SOURCE_DEFAULT, SOURCE_CSV)

  val PREFIX:String = getArgumentParameter("--prefix", defaultStringDeserializer, "","")

  val KML_EXTENSION = "kml"
  val KML_FILE_FILTER = new FileFilter {
    def accept(file: File) = file.extension == KML_EXTENSION
  }
  val KML_FILE_PATH_DESERIALIZER = fileWithTypeDeserializer(KML_EXTENSION)

  private[tools] val PATH_MISSING = "You need to specify a file or folder path as the last argument."
  val PATH:File = (ONE,args.length>0,args.lastOption) match {
    case (true,true,Some(path)) => KML_FILE_PATH_DESERIALIZER(path)
    case (false,true,Some(path)) => folderDeserializer(path)
    case (_,true,None) => throw new IllegalArgumentException(PATH_MISSING)
    case (_,false,_) => null //fail silently since we're not going to use the path anyway
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

  val VALIDITY_DESERIALIZER = (s:String) => Interval.unapply(s) match {
    case Some(interval) => interval
    case _ => throw new IllegalArgumentException("Incorrect interval")
  }

  val VALIDITY:Interval = getArgumentParameter("--valid",VALIDITY_DESERIALIZER,Interval.FOREVER,INTERVAL_FORMAT_MESSAGE)

  val HELP_MESSAGE =
    """
      |Imports one or multiple *.kml files as areas in the database.
      |Usage: %s [--one|--many] [--clear] [--source] FOLDER/FILE
      |--one: Imports just one file as opposed to many of them.
      |--many (default): Recursively imports an entire folder of kml files.
      |--clear: Clears all the areas from the database before importing. Only the areas of the specified --source are removed.
      |--source: The type of area that we are importing. Possible values are %s. Default is %s.
      |--valid: The interval while this is valid. Specify in the format 'YYYYMMDD-YYYYMMDD'. Either interval margins can be omitted.
      |--prefix: Prefix to be placed in front of the computed area name.
    """.stripMargin.format(PROGRAM_NAME,Area.Source.CSV,SOURCE_DEFAULT)

  val areaDao:PoliceAreaDao = ComponentRegistry.policeAreaDao

  override def apply():PoliceKmlTool = {
    super.apply()
    this
  }

  def execute(){
    if(CLEAR) clear()
    val areas = if(ONE) readOne()::Nil else readMany()
    info("Imported %d areas".format(areas.length))
    val saved = writeAll(areas)
    info("Wrote %d areas to database".format(saved.length))
  }

  val MSG_CLEAR_QUESTION="Are you sure you want to clear the database? [y/n]: "
  val MSG_CLEAR_START="Removing from database all areas with source %s."
  val MSG_CLEAR_SKIPPED="Cancelled area database deletion."

  def clear(){
    OUT.print(MSG_CLEAR_QUESTION)
    val sure = IN.read()
    OUT.println("\n")
    if (sure == 'y') {
      OUT.println(MSG_CLEAR_START.format(SOURCE))
      areaDao.deleteBySource(SOURCE)
    }else{
      OUT.println(MSG_CLEAR_SKIPPED)
      throw new RuntimeException("Execution aborted")
    }
  }

  def readOne():PoliceArea = readOne(PATH, Nil)

  protected def readOne(file:File, breadcrumb:Seq[String]):PoliceArea = {
    val dash = if (PREFIX.isEmpty && breadcrumb.isEmpty) "" else "-"
    val path = breadcrumb.foldLeft(new StringBuilder)((sb,b)=> if(sb.isEmpty) sb.append(b) else sb.append("-").append(b))
    val basename = file.nameWithoutExtension
    val name = PREFIX + path + dash + basename
    val policeForce = breadcrumb.lastOption.getOrElse("")
    info("Reading %s".format(file.getAbsolutePath))
    new PoliceArea(-1, name, SOURCE, VALIDITY, Kml.unmarshal(file),policeForce,basename)
  }

  def readMany():Seq[PoliceArea] = readMany(PATH, Nil)

  protected def readMany(dir:File, breadcrumb:Seq[String]):Seq[PoliceArea] = {
    info("Opening dir %s".format(dir.getAbsolutePath))
    dir.listFiles().toSeq.flatMap(file => {
      val newBreadcrumb = breadcrumb :+ dir.getName
      if (file.isDirectory) readMany(file,newBreadcrumb)
      else if (file.extension==KML_EXTENSION) tryo{ readOne(file,newBreadcrumb) }.toSeq
      else Seq()
    })
  }

  def writeAll(areas:Seq[PoliceArea]) = areaDao.saveAll(areas)
}

