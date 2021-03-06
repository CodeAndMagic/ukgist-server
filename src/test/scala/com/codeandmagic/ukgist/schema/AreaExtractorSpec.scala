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
import com.codeandmagic.ukgist.model.PolygonAreaFixture._
import com.codeandmagic.ukgist.dao.PoliceAreaDaoFixture._
import com.codeandmagic.ukgist.util.InvalidKmlException
import com.codeandmagic.ukgist.model.{PoliceCrimeData, Companion, KmlPolygonArea, PoliceArea}
import org.specs2.mock.Mockito
import org.orbroker.Row

/**
 * User: cvrabie
 * Date: 25/04/2013
 */
class AreaExtractorSpec extends Specification with Mockito{
  //force class loading! This raises some questions about the implementation of the discriminator...
  PoliceArea.discriminator
  KmlPolygonArea.discriminator

  "AreaExtractor" should{
    "properly extract a PoliceArea" in{
      val area = AreaExtractor.extract(LONDON_1_POLICE_ROW)
      (area.getClass == classOf[PoliceArea]) must beTrue
      area.name must beEqualTo(LONDON_1_AREA_NAME)
      area.asInstanceOf[PoliceArea].geometry must beAPolygon(LONDON_1_KML_OUTER)
      area.asInstanceOf[PoliceArea].policeForce must beEqualTo(LONDON_1_FORCE)
    }

    "properly extract a KmlPolygonArea" in{
      val area = AreaExtractor.extract(LONDON_1_KML_ROW)
      (area.getClass == classOf[KmlPolygonArea]) must beTrue
      area.name must be(LONDON_1_AREA_NAME)
      area.asInstanceOf[KmlPolygonArea].geometry must beAPolygon(LONDON_1_KML_OUTER)
    }

    "throw an InvalidKmlException if the KML is invalid" in{
      AreaExtractor.extract(BROKEN_ROW) must throwA(manifest[InvalidKmlException])
    }

    "throw a ClassCastException if this is a Discriminator for something else than an Area" in{
      val row = mock[Row]
      row.bigInt("area.id") returns Some(1)
      row.integer("area.discriminator") returns Some(PoliceCrimeData.discriminator)
      AreaExtractor.extract(row) must throwA[ClassCastException]
    }

    "trow a ClassCastException if the discriminator does not exist" in{
      val row = mock[Row]
      row.bigInt("area.id") returns Some(1)
      row.integer("area.discriminator") returns Some(-1)
      AreaExtractor.extract(row) must throwA[ClassCastException]
    }

    "trow a ClassCastException if the row has no information about the discriminator" in{
      val row = mock[Row]
      row.bigInt("area.id") returns Some(1)
      AreaExtractor.extract(row) must throwA[ClassCastException]
    }

    "throw a ClassCastException if this is a Discriminator that is not Persistent" in{
      object SpecialArea extends Companion[PoliceArea]{
        val clazz = manifest[PoliceArea]
        val discriminator = 123
        val row = mock[Row]
        row.integer("area.discriminator") returns Some(123)
        AreaExtractor.extract(row) must throwA[ClassCastException]
      }
    }
  }
}
