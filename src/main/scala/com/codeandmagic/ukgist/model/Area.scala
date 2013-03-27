package com.codeandmagic.ukgist.model

import com.codeandmagic.ukgist.model.Area.BoundingBox

/**
 * User: cvrabie
 * Date: 23/03/2013
 */

/**
 * Defines a grouping for statistics, usually geographic.
 */
abstract class Area(
  val id: Long,
  val name: String,
  val kind: Area.Kind.Value
){
  /**
   * * Minimum Bounding Rectangle (MBR). Basically Latitude and Longitude MIN and MAX values.
   * Used for quick calculations, like checking if the area DOES NOT contain a Location.
   */
  val boundingBox:BoundingBox

  /**
   * Checks if the passed location is within the bounding box of this area.
   * This operation needs to be very fast because is used to quickly search through multiple areas.
   * Use {@link Area#containsDefinitely}  if you want to be sure that this area does contain the location.
   * @param loc
   * @return TRUE if there's a change that this area contains loc. FALSE if definitely loc is not within this area.
   */
  def containsMaybe(loc:Location):Boolean

  /**
   * Checked if the passed location is within this area.
   * This operation is usually slow. {@link Area#containsMaybe} can be used before to quickly eliminate if the location
   * is not in this area.
   * @param loc
   * @return TRUE is this area contains loc. FALSE otherwise.
   */
  def containsDefinitely(loc:Location):Boolean
}

object Area{
  /**
   * Area kind is usually defined by where the area is used.
   * For example police crime data have their own list of areas.
   */
  object Kind extends Enumeration{
    val OTHER, POLICE = Value
    val CSV = values.head.toString +
      values.takeRight(values.size-1).foldLeft(new StringBuilder)((sb,at)=>sb.append(", ").append(at.toString)).toString
  }

  class BoundingBox(val west:Double, val south:Double, val east:Double, val north:Double){
    //lazy val min = new Coordinate(west,south)
    //lazy val max = new Coordinate(east,north)
  }
}

trait AreaDao[T<:Area]{
  /**
   * Deletes from the database all areas with the specified type.
   * @param t
   * @return the number of deleted items
   */
  def deleteByType(t:Area.Kind.Value):Int

  /**
   * Fetches from the database the area with the specified id.
   * @param id
   * @return
   */
  def getById(id:Long):Option[T]

  /**
   * Fetches all the areas from the database.
   * @return
   */
  def listAll():Seq[T]
}

