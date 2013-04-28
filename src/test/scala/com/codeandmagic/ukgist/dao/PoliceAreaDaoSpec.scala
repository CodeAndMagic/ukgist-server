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

package com.codeandmagic.ukgist.dao

import org.specs2.mutable.Specification
import com.codeandmagic.ukgist.model.PolygonAreaFixture._
import PoliceAreaDaoFixture._
import com.codeandmagic.ukgist.util.InvalidKmlException
import com.codeandmagic.ukgist.model.{PoliceArea, KmlPolygonArea, Area}
import java.io.FileInputStream
import org.joda.time.DateTime
import org.orbroker.Row
import java.sql.Timestamp
import com.codeandmagic.ukgist.schema.PoliceAreaExtractor

/**
 * User: cvrabie
 * Date: 25/03/2013
 */
class PoliceAreaDaoSpec extends Specification{

  object MockRegistry extends BrokerComponent with BrokerPoliceAreaDaoComponent{
    val broker = ORBrokerFactory.apply("org.sqlite.JDBC","jdbc:sqlite:src/test/resources/sqlite/area_fixture.sqlite","","")
    val policeAreaDao = new BrokerPoliceAreaDao
  }

  "PoliceAreaDao(area_fixture.sqlite)" should{
    val all = MockRegistry.policeAreaDao.listAll()

    "list all items in the database" in{
      all.size must be_==(2)
    }

    "correctly deserialize a valid Area" in{
      val area = all(0)
      area.name must beEqualTo(LONDON_1_AREA_NAME)
      area.validity must beEqualTo(LONDON_1_AREA_VALIDITY)
      area.geometry must beAPolygon(LONDON_1_KML_OUTER)
      area.policeForce must beEqualTo(LONDON_1_FORCE)
      area.neighborhood must beEqualTo(LONDON_1_NEIGHBORHOOD)
    }

    "throw an InvalidKmlException if the KML is invalid" in{
      PoliceAreaExtractor.extract(BROKEN_ROW) must throwA(manifest[InvalidKmlException])
    }

  }
}

object PoliceAreaDaoFixture{
  def mockKmlRow(id:Long, name:String, source:Area.Source.Value, is:FileInputStream,
                 from:DateTime, to:DateTime) = {
    val row = mock[Row]
    row.bigInt("area_id") returns Some(id)
    row.integer("area_discriminator") returns Some(KmlPolygonArea.discriminator)
    row.string("area_name") returns Some(name)
    row.smallInt("area_source") returns Some(source.id.toShort)
    row.timestamp("area_validity_start") returns Some(new Timestamp(from.getMillis))
    row.timestamp("area_validity_end") returns Some(new Timestamp(to.getMillis))
    row.binaryStream("area_kml") returns Some(is)
    /*return*/ row
  }

  def mockPoliceRow(id:Long, name:String, source:Area.Source.Value, is:FileInputStream,
                    from:DateTime, to:DateTime, force:String, neighborhood:String) = {
    val row = mockKmlRow(id,name,source,is,from,to)
    row.integer("area_discriminator") returns Some(PoliceArea.discriminator)
    row.string("area_police_force") returns Some(force)
    row.string("area_police_neighborhood") returns Some(neighborhood)
    /*return*/ row
  }

  val LONDON_1_POLICE_ROW = mockPoliceRow(1,LONDON_1_AREA_NAME,LONDON_1_AREA_SOURCE,LONDON_1_KML_PATH,LONDON_1_FROM,LONDON_1_TO,LONDON_1_FORCE,LONDON_1_NEIGHBORHOOD)
  val LONDON_1_KML_ROW = mockKmlRow(1,LONDON_1_AREA_NAME,LONDON_1_AREA_SOURCE,LONDON_1_KML_PATH,LONDON_1_FROM,LONDON_1_TO)
  val BROKEN_ROW = mockPoliceRow(1,LONDON_1_AREA_NAME,LONDON_1_AREA_SOURCE,BROKEN_KML_PATH, LONDON_1_FROM, LONDON_1_TO,LONDON_1_FORCE, LONDON_1_NEIGHBORHOOD)
}