package com.codeandmagic.ukgist.tools

import com.codeandmagic.ukgist.model.AreaType

/**
 * User: cvrabie
 * Date: 26/03/2013
 */
object KmlImport extends App{
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

  private val ts = AreaType.values

  val TYPE_VALUES = ts.head.toString + ts.takeRight(ts.size-1).foldLeft(new StringBuilder)(
    (sb,at)=>sb.append(", ").append(at.toString)).toString

  val TYPE_DEFAULT = AreaType.POLICE

  val TYPE:AreaType.Value = argsWithIndex.find(_ match {
    case ("--type",i) if (i < args.length-1 && !args(i+1).startsWith("--")) => true
    case ("--type",_) => throw new IllegalArgumentException("You need to specify a type. Possible values %s".format(TYPE_VALUES))
    case _ => false
  }) match {
    case Some((_,i)) => AreaType.withName(args(i+1))
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
    """.stripMargin.format(PROGRAM_NAME,TYPE_VALUES,TYPE_DEFAULT)

  def apply() =
    if (args.size>1) doStuff()
    else help()

  def help() = println(HELP_MESSAGE)

  def doStuff(){
    println("""Debug: ONE(%b), CLEAR(%b), TYPE(%s)""".format(ONE,CLEAR,TYPE))
  }
}


