package com.codeandmagic.ukgist

import org.specs2.mutable.Specification
import schema.KmlAreaExtractor
import de.micromata.opengis.kml.v_2_2_0._
import KmlAreaFixture._

/**
 * User: cvrabie
 * Date: 25/03/2013
 */
class KmlAreaExtractorSpec extends Specification{

  "KmlAreaExtractor" should{

    "correctly deserialize a valid KML" in{
      val kmlArea = KmlAreaExtractor.extract(LONDON_1_KML_ROW)
      kmlArea.name must be(LONDON_1_AREA_NAME)
      kmlArea.kml must beSome.which(_.isInstanceOf[Kml])
      kmlArea.kml.get must beAPolygon(LONDON_1_KML_OUTER)
    }

    "fail silently if the KML is invalid" in{
      val kmlArea = KmlAreaExtractor.extract(BROKEN_ROW)
      kmlArea.name must be(LONDON_1_AREA_NAME)
      kmlArea.kml must beNone
    }

  }
}