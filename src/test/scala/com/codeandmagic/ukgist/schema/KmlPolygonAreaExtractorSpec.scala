package com.codeandmagic.ukgist.schema

import org.specs2.mutable.Specification
import de.micromata.opengis.kml.v_2_2_0._
import com.codeandmagic.ukgist.model.PolygonAreaFixture._
import com.codeandmagic.ukgist.util.InvalidKmlException

/**
 * User: cvrabie
 * Date: 25/03/2013
 */
class KmlPolygonAreaExtractorSpec extends Specification{

  "KmlPolygonAreaExtractor" should{

    "correctly deserialize a valid KML" in{
      val area = KmlPolygonAreaExtractor.extract(LONDON_1_KML_ROW)
      area.name must be(LONDON_1_AREA_NAME)
      area.geometry must beAPolygon(LONDON_1_KML_OUTER)
    }

    "fail silently if the KML is invalid" in{
      KmlPolygonAreaExtractor.extract(BROKEN_ROW) must throwA(manifest[InvalidKmlException])
    }

  }
}