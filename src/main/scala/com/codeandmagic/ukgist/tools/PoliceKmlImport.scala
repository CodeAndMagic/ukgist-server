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
import net.liftweb.common.Logger
import com.codeandmagic.ukgist.dao._
import scala.Some
import java.util.concurrent.LinkedBlockingQueue
import java.util

/**
 * User: cvrabie
 * Date: 26/03/2013
 */
object PoliceKmlImport extends App {
  override def main(args: Array[String]) {
    super.main(args)

    object ComponentRegistry extends PoliceKmlToolComponent with BrokerComponent with BrokerPoliceAreaDaoComponent{
      val broker = ORBrokerFactory.fromProps()
      val policeAreaDao = new BrokerPoliceAreaDao
      val policeKmlImportTool = new PoliceKmlTool(args:_*)
    }

    ComponentRegistry.policeKmlImportTool.apply()
  }
}

trait PoliceKmlToolComponent{
  this:PoliceAreaDaoComponent =>

  val policeKmlImportTool:PoliceKmlTool

/**
 * Tool that inserts into the database [[com.codeandmagic.ukgist.model.PolygonArea]]s based on the KML files
 * provided by the Police Data website http://www.police.uk/data
 */
class PoliceKmlTool(args:String*) extends ProducerConsumerTool[PoliceArea](args:_*) with Logger{
  val REQUIRED_PARAMETERS = 1

  val cls = classOf[PoliceArea]

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
    case (_,true,Some("--help")) => null
    case (true,true,Some(path)) => KML_FILE_PATH_DESERIALIZER(path)
    case (false,true,Some(path)) => folderDeserializer(path)
    case (_,true,None) => throw new IllegalArgumentException(PATH_MISSING)
    case (_,false,_) => null //fail silently since we're not going to use the path anyway
  }

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

  override def apply():PoliceKmlTool = {
    super.apply()
    this
  }

  override def execute() {
    if(CLEAR) clear()
    super.execute()
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
      policeAreaDao.deleteBySource(SOURCE)
    }else{
      OUT.println(MSG_CLEAR_SKIPPED)
      throw new RuntimeException("Execution aborted")
    }
  }

  def readOne(){
    readOne(PATH, Nil)
  }

  protected def readOne(file:File, breadcrumb:Seq[String]){
    val dash = if (PREFIX.isEmpty && breadcrumb.isEmpty) "" else "-"
    val path = breadcrumb.foldLeft(new StringBuilder)((sb,b)=> if(sb.isEmpty) sb.append(b) else sb.append("-").append(b))
    val basename = file.nameWithoutExtension
    val name = PREFIX + path + dash + basename
    val policeForce = breadcrumb.lastOption.getOrElse("")
    info("Reading %s".format(file.getAbsolutePath))
    QUEUE.put(
      new PoliceArea(-1, name, SOURCE, VALIDITY, Kml.unmarshal(file),policeForce,basename)
    )
  }

  def readMany(){ readMany(PATH, Nil) }

  protected def readMany(dir:File, breadcrumb:Seq[String]) {
    info("Opening dir %s".format(dir.getAbsolutePath))
    dir.listFiles().toSeq.foreach(file => {
      val newBreadcrumb = breadcrumb :+ dir.getName
      if (file.isDirectory) readMany(file,newBreadcrumb)
      else if (file.extension==KML_EXTENSION) tryo{
        readOne(file,newBreadcrumb)
      }
    })
  }

  def processBatch(items: Seq[PoliceArea]) = policeAreaDao.saveAll(items)
}
}

