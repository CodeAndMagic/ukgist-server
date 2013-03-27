package com.codeandmagic.ukgist.tools

import com.codeandmagic.ukgist.model.{Area, AreaDao, PolygonArea}
import com.vividsolutions.jts.geom.{Polygon => JstPolygon}
import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.util.KmlUtils

/**
 * User: cvrabie
 * Date: 26/03/2013
 */
object PoliceKmlImport extends App{

  override def main(args: Array[String]) {
    super.main(args)
    apply()
  }
  val argsWithIndex = args.zipWithIndex
  private val name = getClass.getSimpleName
  val PROGRAM_NAME = name.substring(0,name.indexOf("$") match {
    case i if i>0 => i
    case _ => name.length
  })

  if (args.contains("--one") && args.contains("--many"))
    throw new IllegalArgumentException("Do you want --one or --many?")
  val ONE = args.contains("--one")
  val CLEAR = args.contains("--clear")



  val TYPE_DEFAULT = Area.Kind.POLICE

  val TYPE:Area.Kind.Value = argsWithIndex.find(_ match {
    case ("--type",i) if (i < args.length-1 && !args(i+1).startsWith("--")) => true
    case ("--type",_) => throw new IllegalArgumentException("You need to specify a type. Possible values %s".format(Area.Kind.CSV))
    case _ => false
  }) match {
    case Some((_,i)) => try{ Area.Kind.withName(args(i+1)) }
      catch { case e:NoSuchElementException => throw new NoSuchElementException("No Area type %s. Possible values are %s".format(args(i+1),Area.Kind.CSV)) }
    case None => TYPE_DEFAULT
  }

  val HELP_MESSAGE =
    """
      |Imports one or multiple *.kml files as areas in the database.
      |Usage: %s [--one|--many] [--clear] [--type] FOLDER/FILE
      |--one: Imports just one file as opposed to many of them.
      |--many (default): Recursively imports an entire folder of kml files.
      |--clear: Clears all the areas from the database before importing. Only the areas of the specified --type are removed.
      |--type: The type of area that we are importing. Possible values are %s. Default is %s.
    """.stripMargin.format(PROGRAM_NAME,Area.Kind.CSV,TYPE_DEFAULT)

  val areaDao:AreaDao[PolygonArea] = PolygonArea

  def apply() =
    if (args.size>1) doStuff()
    else help()

  def help() = println(HELP_MESSAGE)

  def doStuff(){
    println("""Debug: ONE(%b), CLEAR(%b), TYPE(%s)""".format(ONE,CLEAR,TYPE))
  }

  def clear(){
    areaDao.deleteByType(TYPE)
  }
}

