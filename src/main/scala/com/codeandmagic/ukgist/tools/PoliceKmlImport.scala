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

import com.codeandmagic.ukgist.model.{KmlPolygonArea, Area, AreaDao, PolygonArea}
import com.codeandmagic.ukgist.util.FileOps._
import com.vividsolutions.jts.geom.{Polygon => JstPolygon}
import java.io.{FileFilter, File}
import net.liftweb.util.Helpers.tryo
import de.micromata.opengis.kml.v_2_2_0.Kml

/**
 * User: cvrabie
 * Date: 26/03/2013
 */
object PoliceKmlImport extends App{
  override def main(args: Array[String]) {
    super.main(args)
    new PoliceKmlTool(args:_*).apply()
  }
}
/**
 * Tool that inserts into the database [[com.codeandmagic.ukgist.model.PolygonArea]]s based on the KML files
 * provided by the Police Data website http://www.police.uk/data
 */
class PoliceKmlTool(override val args:String*) extends Tool(args:_*){
  val REQUIRED_PARAMETERS = 1

  if (args.contains("--one") && args.contains("--many"))
    throw new IllegalArgumentException("Do you want --one or --many?")
  val ONE = args.contains("--one")
  val CLEAR = args.contains("--clear")

  val KIND_DEFAULT = Area.Kind.POLICE

  val KIND_CSV = "Possible values are %s".format(Area.Kind.CSV)

  val areaKindDesearializer = (s:String) => try{ Area.Kind.withName(s) }
    catch { case e:NoSuchElementException => throw new NoSuchElementException("No suck Area.Kind %s. %s".format(s,KIND_CSV)) }

  val KIND:Area.Kind.Value = getArgumentParameter("--kind", areaKindDesearializer, KIND_DEFAULT, KIND_CSV)

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

  val HELP_MESSAGE =
    """
      |Imports one or multiple *.kml files as areas in the database.
      |Usage: %s [--one|--many] [--clear] [--kind] FOLDER/FILE
      |--one: Imports just one file as opposed to many of them.
      |--many (default): Recursively imports an entire folder of kml files.
      |--clear: Clears all the areas from the database before importing. Only the areas of the specified --kind are removed.
      |--kind: The type of area that we are importing. Possible values are %s. Default is %s.
      |--prefix: Prefix to be placed in front of the computed area name.
    """.stripMargin.format(PROGRAM_NAME,Area.Kind.CSV,KIND_DEFAULT)

  val areaDao:AreaDao[PolygonArea] = PolygonArea

  override def apply():PoliceKmlTool = {
    super.apply()
    this
  }

  def execute(){
    if(CLEAR) clear()
    val areas = if(ONE) readOne()::Nil else readMany()
    writeAll(areas)
  }

  val MSG_CLEAR_QUESTION="Are you sure you want to clear the database? [y/n]: "
  val MSG_CLEAR_START="Removing from database all areas of type %s."
  val MSG_CLEAR_SKIPPED="Cancelled area database deletion."

  def clear(){
    OUT.print(MSG_CLEAR_QUESTION)
    val sure = IN.read()
    OUT.println("\n")
    if (sure == 'y') {
      OUT.println(MSG_CLEAR_START.format(KIND))
      areaDao.deleteByType(KIND)
    }else{
      OUT.println(MSG_CLEAR_SKIPPED)
      throw new RuntimeException("Execution aborted")
    }
  }

  def readOne():KmlPolygonArea = readOne(PATH, "")

  protected def readOne(file:File, breadcrumb:String):KmlPolygonArea = {
    val dash = if (PREFIX.isEmpty && breadcrumb.isEmpty) "" else "-"
    val name = PREFIX + breadcrumb + dash + file.nameWithoutExtension
    new KmlPolygonArea(-1, name, KIND, Kml.unmarshal(file))
  }

  def readMany():Seq[KmlPolygonArea] = readMany(PATH, "")

  protected def readMany(dir:File, breadcrumb:String):Seq[KmlPolygonArea] = dir.listFiles().toSeq.flatMap(file => {
    val dash = if(breadcrumb.isEmpty) "" else  "-"
    val newBreadcrumb = breadcrumb+dash+dir.getName
    if (file.isDirectory) readMany(file,newBreadcrumb)
    else if (file.extension==KML_EXTENSION) tryo{ readOne(file,newBreadcrumb) }.toSeq
      else Seq()
  })

  def writeAll(areas:Seq[KmlPolygonArea]) = null
}

