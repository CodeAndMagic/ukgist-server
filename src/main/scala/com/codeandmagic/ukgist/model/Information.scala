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

/**
 * User: cvrabie
 * Date: 24/04/2013
 */
class Information(override val id:Long, val discriminator:Int, val areaId:Area, val validity:Interval) extends Entity(id){
  def copyWithId(newId: Long) = new Information(newId, discriminator, areaId, validity)
  def companion:Companion[_<:Information] = Information
}

object Information extends Persistent[Information]{
  val clazz = manifest[Information]
  def extractor = InformationExtractor
}

abstract class InformationExtension(override val id:Long, val information:Information, val area:Area) extends Entity(id){
  def companion:Companion[_<:InformationExtension]
}
