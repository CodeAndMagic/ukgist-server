package com.codeandmagic.ukgist.schema

import org.orbroker.{Row, RowExtractor}
import com.codeandmagic.ukgist.model.{KmlArea}
import de.micromata.opengis.kml.v_2_2_0.Kml

/**
 * User: cvrabie1
 * Date: 23/03/2013
 */
object KmlAreaExtractor extends RowExtractor[KmlArea]{
  def extract(row: Row) = new KmlArea(
    row.bigInt("id").get,
    row.string("name").get,
    row.binaryStream("kml").flatMap(Kml.unmarshal(_) match {
      case kml:Kml if KmlArea.validKml(kml) => Some(kml)
      case _ => None
    })
  )

  object WithoutKml extends RowExtractor[KmlArea]{
    def extract(row: Row) = new KmlArea(
      row.bigInt("id").get,
      row.string("name").get,
      None
    )
  }
}
