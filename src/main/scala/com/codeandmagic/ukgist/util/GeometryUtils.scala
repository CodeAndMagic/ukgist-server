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

package com.codeandmagic.ukgist.util

import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory
import com.vividsolutions.jts.geom.{Envelope, Coordinate, Geometry, GeometryFactory}
import com.codeandmagic.ukgist.model.Location
import com.codeandmagic.ukgist.model.Area.BoundingBox

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
object GeometryUtils {

  val coordinateFactory = CoordinateArraySequenceFactory.instance()
  val geometryFactory = new GeometryFactory(coordinateFactory)

  /**
   * Maps a geographical coordinate to a JTS coordinate. Longitude is X and Latitude is Y.
   * @param l
   * @return
   */
  implicit def locationToCoordinate(l:Location):Coordinate = new Coordinate(l.lng,l.lat)

  /**
   * Maps a geographical coordinate to a JTS [[com.vividsolutions.jts.geom.Point]].
   * @param l
   * @return
   * @see locationToCoordinate
   */
  implicit def locationToGeometry(l:Location):Geometry = geometryFactory.createPoint(locationToCoordinate(l))

  /**
   * Maps a geographical coordinate to a JTS [[com.vividsolutions.jts.geom.Envelope]]
   * @param l
   * @return
   */
  implicit def locationToEnvelope(l:Location):Envelope = new Envelope(locationToCoordinate(l))

  /**
   * Maps a [[com.codeandmagic.ukgist.model.Area.BoundingBox]] to a JTS [[com.vividsolutions.jts.geom.Envelope]].
   * Longitude is X and Latitude is Y.
   * @param bb
   * @return
   */
  implicit def boundingBoxToEnvelope(bb:BoundingBox) = new Envelope(bb.west,bb.east,bb.south,bb.north)

}
