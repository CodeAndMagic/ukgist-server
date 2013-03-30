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