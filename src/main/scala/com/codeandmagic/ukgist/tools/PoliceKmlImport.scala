package com.codeandmagic.ukgist.tools

import com.codeandmagic.ukgist.model.{KmlPolygonArea, Area, AreaDao, PolygonArea}
import com.vividsolutions.jts.geom.{Polygon => JstPolygon}
import java.io.File

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

  val KML_FILE_PATH_DESERIALIZER = fileWithTypeDeserializer("kml")

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

  private[tools] val CLEAR_QUESTION="Are you sure you want to clear the database? [y/n]: "
  private[tools] val CLEAR_START="Removing from database all areas of type %s."
  private[tools] val CLEAR_SKIPPED="Cancelled area database deletion."

  def clear(){
    OUT.print(CLEAR_QUESTION)
    val sure = IN.read()
    OUT.println("\n")
    if (sure == 'y') {
      OUT.println(CLEAR_START.format(KIND))
      areaDao.deleteByType(KIND)
    }else{
      OUT.println(CLEAR_SKIPPED)
      throw new RuntimeException("Execution aborted")
    }
  }

  def readOne():KmlPolygonArea = null
  def readMany():Seq[KmlPolygonArea] = null
  def writeAll(areas:Seq[KmlPolygonArea]) = null
}

