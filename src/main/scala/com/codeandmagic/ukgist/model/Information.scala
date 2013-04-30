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

import com.codeandmagic.ukgist.schema.InformationExtractor
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.JsonAST.JInt
import net.liftweb.json.JsonAST.JObject

/**
 * User: cvrabie
 * Date: 24/04/2013
 */
class Information(
  override val id:Int,
  val discriminator:Int,
  val area:Area,
  val validity:Interval
) extends Entity(id){
  def copyWithId(newId: Int) = new Information(newId, discriminator, area, validity)
  def companion:Companion[_<:Information] = Information

  protected lazy val json = JObject(List(
    JField("id",JInt(id)),
    JField("discriminator",Discriminator.findByDiscriminator(discriminator)
      .map(d=>JString(d.clazz.erasure.getSimpleName)).getOrElse(JNull)),
    JField("area",area.toJson),
    JField("validity",validity.toJson)
  ))
  def toJson = json
}

object Information extends Companion[Information]{
  val clazz = manifest[Information]
  def extractor = InformationExtractor
}

abstract class InformationExtension(override val id:Int, val information:Information) extends Entity(id){
  def companion:Companion[_<:InformationExtension]

  protected def fields = List(
    JField("id",JInt(id)),
    JField("discriminator",JString(companion.clazz.erasure.getSimpleName)),
    JField("information", information.toJson)
  )

  protected lazy val json = JObject(fields)

  def toJson = json
}
