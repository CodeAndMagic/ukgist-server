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

import com.codeandmagic.ukgist.schema.PoliceCrimeDataExtractor

/**
 * User: cvrabie
 * Date: 24/04/2013
 */
class PoliceCrimeData(
  override val id:Long,
  override val information: Information,
  override val area: Area,
  val allCrime:Int,
  val antiSocialBehaviour:Int,
  val burglary:Int,
  val criminalDamage:Int,
  val drugs:Int,
  val otherTheft:Int,
  val publicDisorder:Int,
  val robbery:Int,
  val shoplifting:Int,
  val vehicleCrime:Int,
  val violentCrime:Int,
  val otherCrime:Int
) extends InformationExtension(id, information, area){

  def copyWithId(newId: Long) = new PoliceCrimeData( id = newId, information=information, area=area, allCrime = allCrime,
    antiSocialBehaviour = antiSocialBehaviour, burglary = burglary, criminalDamage = criminalDamage, drugs = drugs,
    otherTheft = otherTheft, publicDisorder = publicDisorder, robbery = robbery, shoplifting = shoplifting,
    vehicleCrime = vehicleCrime, violentCrime = violentCrime, otherCrime = otherCrime)

  def companion:Companion[_<:PoliceCrimeData] = PoliceCrimeData
}

object PoliceCrimeData extends Persistent[PoliceCrimeData]{
  def extractor = PoliceCrimeDataExtractor
  val clazz = manifest[PoliceCrimeData]
}
