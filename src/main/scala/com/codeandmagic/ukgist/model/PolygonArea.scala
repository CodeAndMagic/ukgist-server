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

import com.codeandmagic.ukgist.schema.{KmlAreaSchemaTokens, ORBrokerHelper}
import com.vividsolutions.jts.geom.{Coordinate, Polygon}
import com.codeandmagic.ukgist.model.Area.BoundingBox
import com.codeandmagic.ukgist.util.GeometryUtils.{locationToGeometry,locationToCoordinate}

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
/**
 * Area defined by a polygon.
 * @param id
 * @param name
 * @param kind
 * @param geometry
 */
class PolygonArea(
  override val id: Long,
  override val name: String,
  override val kind:Area.Kind.Value,
  val geometry: Polygon
)
extends Area(id,name,kind){

  protected val bb = geometry.getEnvelopeInternal
  val boundingBox = new BoundingBox(bb.getMinX, bb.getMinY, bb.getMaxX, bb.getMaxY)

  def containsMaybe(loc: Location) = bb.intersects(loc:Coordinate)
  def containsDefinitely(loc: Location) = geometry.intersects(loc)

}

object PolygonArea extends AreaDao[PolygonArea]{
  def getById(id: Long) = ORBrokerHelper.broker.readOnly()(
    _.selectOne(KmlAreaSchemaTokens.getById, "id"->id)
  )

  def listAll() = ORBrokerHelper.broker.readOnly()(
    _.selectAll(KmlAreaSchemaTokens.listAll)
  )

  def deleteByType(t: Area.Kind.Value) = ORBrokerHelper.broker.transaction()(
    _.execute(KmlAreaSchemaTokens.deleteByType, "t"->t)
  )
}



