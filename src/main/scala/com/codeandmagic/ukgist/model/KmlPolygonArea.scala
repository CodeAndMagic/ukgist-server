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

import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.util.KmlUtils
import com.codeandmagic.ukgist.schema.KmlAreaExtractor
import net.liftweb.json.JsonAST.{JString, JField}

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
class KmlPolygonArea(override val id:Long,
                          override val name:String,
                          override val source:Area.Source.Value,
                          override val validity: Interval,
                          val kml:Kml)
  extends PolygonArea(id,name,source,validity,KmlUtils.kmlPolygonToJtsPolygon(kml)){
  override def companion:Companion[_<:KmlPolygonArea] = KmlPolygonArea
  override def copyWithId(newId: Long):KmlPolygonArea = new KmlPolygonArea(newId, name, source, validity, kml)

  override protected def fields = super.fields.filterNot(_.name=="geometry") ++ List(
    JField("discriminator",JString(companion.clazz.erasure.getSimpleName)),
    JField("kml", JString(kml.toString))
  )
}

object KmlPolygonArea extends Persistent[KmlPolygonArea]{
  val clazz = manifest[KmlPolygonArea]
  def extractor = KmlAreaExtractor
}