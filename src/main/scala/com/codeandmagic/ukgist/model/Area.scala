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

package com.codeandmagic.ukgist.model

import com.codeandmagic.ukgist.model.Area.BoundingBox
import net.liftweb.json.JsonAST.{JString, JInt, JField, JObject}

/**
 * User: cvrabie
 * Date: 23/03/2013
 */

/**
 * Defines a grouping for statistics, usually geographic.
 */
abstract class Area(
  override val id: Long,
  val name: String,
  val source: Area.Source.Value,
  val validity: Interval
) extends Entity(id) {

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

  def companion:Companion[_<:Area]

  protected def fields = List(
    JField("id", JInt(id)),
    JField("name", JString(name)),
    JField("source", JString(source.toString)),
    JField("validity", JString(validity.toString))
  )

  protected lazy val json = JObject(fields)

  def toJson = json
}

object Area{
  /**
   * Area source is usually defined by where the area is used.
   * For example police crime data have their own list of areas.
   */
  object Source extends Enumeration{
    val OTHER, POLICE = Value
    val CSV = values.head.toString +
      values.takeRight(values.size-1).foldLeft(new StringBuilder)((sb,at)=>sb.append(", ").append(at.toString)).toString
  }

  class BoundingBox(val west:Double, val south:Double, val east:Double, val north:Double){
    //lazy val min = new Coordinate(west,south)
    //lazy val max = new Coordinate(east,north)
  }
}