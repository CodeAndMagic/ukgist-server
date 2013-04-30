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

import org.orbroker.RowExtractor
import scala.collection.mutable
import net.liftweb.json.JsonAST.{JArray, JInt, JField, JObject}

/**
 * User: cvrabie
 * Date: 12/04/2013
 */
abstract class Entity(val id:Int) {
  def companion:Companion[_<:Entity]
  def copyWithId(newId: Int):Entity
  def toJson:JObject
}

trait Companion[T]{
  val clazz:Manifest[T]
}

trait Persistent[T] extends Companion[T]{
  val discriminator:Int = Discriminator(this)
  def extractor:RowExtractor[T]
}

object Discriminator{
  val values = new mutable.HashMap[Int,Persistent[_]]() with mutable.SynchronizedMap[Int,Persistent[_]]

  def apply(d:Persistent[_]) = {
    val id = d.getClass.getName.hashCode
    values += ((id,d))
    /*return*/ id
  }
  def findByDiscriminator(id:Int) = values.get(id)
}

class Page(items:Iterable[_<:Entity], totalCount:Int, offset:Int){
  def this(items:Iterable[_<:Entity]) = this(items,items.size,0)
  protected lazy val json = JObject(List(
    JField("count",JInt(items.size)),
    JField("totalCount",JInt(totalCount)),
    JField("intems",JArray(items.toList.map(_.toJson)))
  ))
  def toJson:JObject = json
}
