package com.codeandmagic.ukgist.schema

import org.orbroker.{Row, RowExtractor}
import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.util.{KmlUtils, InvalidKmlException}
import com.codeandmagic.ukgist.model.{Area, PolygonArea}

/**
 * User: cvrabie
 * Date: 23/03/2013
 * RowExtractor that extracts a [[com.codeandmagic.ukgist.model.PolygonArea]] stored using a KML
 */
object KmlPolygonAreaExtractor extends RowExtractor[PolygonArea]{
  def extract(row: Row) = new PolygonArea(
    row.bigInt("id").get,
    row.string("name").get,
    row.smallInt("kind").map(Area.Kind(_)).get,
    row.binaryStream("kml") match {
      case Some(is) => Kml.unmarshal(is) match {
        case kml:Kml => KmlUtils.kmlPolygonToJtsPolygon(kml)
        case _ => throw new InvalidKmlException("Blob could not be deserialized to KML.")
      }
      case _ => throw new InvalidKmlException("This area doesn't seem to have a valid KML blob.")
    }
  )
}
