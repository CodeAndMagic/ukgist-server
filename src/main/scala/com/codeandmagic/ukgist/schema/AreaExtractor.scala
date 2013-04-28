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
import com.codeandmagic.ukgist.model._
import org.joda.time.DateTime
import com.codeandmagic.ukgist.util.InvalidKmlException
import scala.Some
import java.io.ByteArrayInputStream

object KmlAreaExtractor extends RowExtractor[KmlPolygonArea]{
  def extract(row: Row) = new KmlPolygonArea(
    row.bigInt("area_id").get,
    row.string("area_name").get,
    row.smallInt("area_source").map(Area.Source(_)).get,
    new Interval(
      row.timestamp("area_validity_start").map(new DateTime(_)),
      row.timestamp("area_validity_end").map(new DateTime(_))
    ),
    row.binary("area_kml") match {
      case Some(bytes) => Kml.unmarshal(new ByteArrayInputStream(bytes)) match {
        case kml:Kml => kml
        case _ => throw new InvalidKmlException("Blob could not be deserialized to KML.")
      }
      case _ => throw new InvalidKmlException("This area doesn't seem to have a valid KML blob.")
    }
  )
}

/**
 * User: cvrabie
 * Date: 23/03/2013
 * RowExtractor that extracts a [[com.codeandmagic.ukgist.model.PoliceArea]] stored using a KML
 */
object PoliceAreaExtractor extends RowExtractor[PoliceArea]{
  def extract(row: Row) = new PoliceArea(
    row.bigInt("area_id").get,
    row.string("area_name").get,
    row.smallInt("area_source").map(Area.Source(_)).get,
    new Interval(
      row.timestamp("area_validity_start").map(new DateTime(_)),
      row.timestamp("area_validity_end").map(new DateTime(_))
    ),
    row.binary("area_kml") match {
      case Some(bytes) => Kml.unmarshal(new ByteArrayInputStream(bytes)) match {
        case kml:Kml => kml
        case _ => throw new InvalidKmlException("Blob could not be deserialized to KML.")
      }
      case _ => throw new InvalidKmlException("This area doesn't seem to have a valid KML blob.")
    },
    row.string("area_police_force").get,
    row.string("area_police_neighborhood").get
  )
}

object AreaExtractor extends RowExtractor[Area]{
  def extract(row: Row) = row.integer("area_discriminator") match {
    case Some(discriminator) => Discriminator.findByDiscriminator(discriminator) match {
      //some voodoo magic
      case Some(instance:Persistent[_]) if instance.clazz <:< manifest[Area] =>
        instance.asInstanceOf[Persistent[_<:Area]].extractor.extract(row)
      case _ => throw new ClassCastException(("Discriminator %d is unknown or not for an Area. Is the discriminator " +
        "stable and the class holding it loaded? Has the class name changed?").format(row.integer("area_discriminator").get))
    }
    case _ => throw new ClassCastException("Row holds no information about discriminator!")
  }
}