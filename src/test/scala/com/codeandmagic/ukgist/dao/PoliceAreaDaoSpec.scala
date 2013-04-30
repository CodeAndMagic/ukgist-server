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
import com.codeandmagic.ukgist.model.{PoliceArea, KmlPolygonArea, Area}
import org.joda.time.DateTime
import org.orbroker.Row
import java.sql.Timestamp
import com.codeandmagic.ukgist.schema.PoliceAreaExtractor
import net.liftweb.common.Logger
import java.io._
import scala.Some
import com.codeandmagic.ukgist.util.InvalidKmlException

/**
 * User: cvrabie
 * Date: 25/03/2013
 */
class PoliceAreaDaoSpec extends Specification with Logger{

  object ReadMockRegistry extends BrokerComponent with BrokerPoliceAreaDaoComponent{
    val broker = ORBrokerFactory.apply("org.sqlite.JDBC","jdbc:sqlite:src/test/resources/sqlite/area_fixture.sqlite","","")
    //val broker = ORBrokerFactory.apply("org.sqlite.JDBC","jdbc:hsqldb:file:src/test/resources/sqlite/area_fixture.sql;ifexists=false;shutdown=true","","")
    val policeAreaDao = new BrokerPoliceAreaDao
  }

  object WriteMockRegistry extends BrokerComponent with BrokerPoliceAreaDaoComponent{
    val database = makeDatabaseFile()
    val broker = ORBrokerFactory.apply("org.sqlite.JDBC","jdbc:sqlite:"+database,"","")
    //val broker = ORBrokerFactory.apply("org.sqlite.JDBC","jdbc:hsqldb:file:"+database+";ifexists=false;shutdown=true","","")
    val policeAreaDao = new BrokerPoliceAreaDao


    def makeDatabaseFile() = {
      val templatePath = Thread.currentThread().getContextClassLoader().getResource("sqlite/area_fixture.sqlite").getPath
      //val templatePath = Thread.currentThread().getContextClassLoader().getResource("sqlite/area_fixture.sql").getPath
      val templateStream = new FileInputStream(templatePath)
      //val databaseFile = new File(System.getProperty("java.io.tmpdir"),"area.sqlite")
      val databaseFile = new File(System.getProperty("java.io.tmpdir"),"area.sql")
      databaseFile.createNewFile()
      val databaseStream = new FileOutputStream(databaseFile, false)
      databaseStream.getChannel.transferFrom(templateStream.getChannel,0,Long.MaxValue) //copy file
      databaseFile.getAbsolutePath
    }
  }

  "PoliceAreaDao(area_fixture.sqlite)" should{
    val all = ReadMockRegistry.policeAreaDao.listAll()

    "list all items in the database" in{
      all.size must be_==(2)
    }

    "correctly deserialize a valid Area" in{
      val area = all(0)
      area.name must beEqualTo(LONDON_1_AREA_NAME)
      area.validity must beEqualTo(LONDON_1_AREA_VALIDITY)
      area.geometry must beAPolygon(LONDON_1_KML_OUTER)
      area.policeForce must beEqualTo(LONDON_1_FORCE)
      area.policeNeighborhood must beEqualTo(LONDON_1_NEIGHBORHOOD)
    }

    "throw an InvalidKmlException if the KML is invalid" in{
      PoliceAreaExtractor.extract(BROKEN_ROW) must throwA(manifest[InvalidKmlException])
    }

    "correctly save a batch of Areas" in{
      val toSave = LONDON_1_POLICE_AREA :: Nil
      val saved = WriteMockRegistry.policeAreaDao.saveAll(toSave)
      saved.size must beEqualTo(toSave.length)
      saved(0).id must be_>(0) and be_!=(toSave(0).id)
    }
  }
}

object PoliceAreaDaoFixture{
  def mockKmlRow(id:Int, name:String, source:Area.Source.Value, bytes:Array[Byte], from:DateTime, to:DateTime) = {
    val row = mock[Row]
    row.integer("area_id") returns Some(id)
    row.integer("area_discriminator") returns Some(KmlPolygonArea.discriminator)
    row.string("area_name") returns Some(name)
    row.smallInt("area_source") returns Some(source.id.toShort)
    row.timestamp("area_validity_start") returns Some(new Timestamp(from.getMillis))
    row.timestamp("area_validity_end") returns Some(new Timestamp(to.getMillis))
    row.binary("area_kml") returns Some(bytes)
    /*return*/ row
  }

  def mockPoliceRow(id:Int, name:String, source:Area.Source.Value, bytes:Array[Byte],
                    from:DateTime, to:DateTime, force:String, neighborhood:String) = {
    val row = mockKmlRow(id,name,source,bytes,from,to)
    row.integer("area_discriminator") returns Some(PoliceArea.discriminator)
    row.string("area_police_force") returns Some(force)
    row.string("area_police_neighborhood") returns Some(neighborhood)
    /*return*/ row
  }

  val LONDON_1_POLICE_ROW = mockPoliceRow(1,LONDON_1_AREA_NAME,LONDON_1_AREA_SOURCE,LONDON_1_KML_PATH,LONDON_1_FROM,LONDON_1_TO,LONDON_1_FORCE,LONDON_1_NEIGHBORHOOD)
  val LONDON_1_KML_ROW = mockKmlRow(1,LONDON_1_AREA_NAME,LONDON_1_AREA_SOURCE,LONDON_1_KML_PATH,LONDON_1_FROM,LONDON_1_TO)
  val BROKEN_ROW = mockPoliceRow(1,LONDON_1_AREA_NAME,LONDON_1_AREA_SOURCE,BROKEN_KML_PATH, LONDON_1_FROM, LONDON_1_TO,LONDON_1_FORCE, LONDON_1_NEIGHBORHOOD)

  val LONDON_1_POLICE_AREA = new PoliceArea(
    id = -1, name=LONDON_1_AREA_NAME, source=LONDON_1_AREA_SOURCE, validity=LONDON_1_AREA_VALIDITY, kml=LONDON_1_KML,
    policeForce = LONDON_1_FORCE, policeNeighborhood = LONDON_1_NEIGHBORHOOD
  )
}