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

package com.codeandmagic.ukgist.schema

import org.orbroker.{Row, RowExtractor}
import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.util.InvalidKmlException
import com.codeandmagic.ukgist.model.{PoliceArea, Interval, KmlPolygonArea, Area}
import org.joda.time.DateTime

/**
 * User: cvrabie
 * Date: 23/03/2013
 * RowExtractor that extracts a [[com.codeandmagic.ukgist.model.PolygonArea]] stored using a KML
 */
object PoliceAreaExtractor extends RowExtractor[PoliceArea]{
  def extract(row: Row) = new PoliceArea(
    row.bigInt("id").get,
    row.string("name").get,
    row.smallInt("source").map(Area.Source(_)).get,
    new Interval(
      row.timestamp("validity_start").map(new DateTime(_)),
      row.timestamp("validity_end").map(new DateTime(_))
    ),
    row.binaryStream("kml") match {
      case Some(is) => Kml.unmarshal(is) match {
        case kml:Kml => kml
        case _ => throw new InvalidKmlException("Blob could not be deserialized to KML.")
      }
      case _ => throw new InvalidKmlException("This area doesn't seem to have a valid KML blob.")
    },
    row.string("police_force").get,
    row.string("police_neighborhood").get
  )
}
