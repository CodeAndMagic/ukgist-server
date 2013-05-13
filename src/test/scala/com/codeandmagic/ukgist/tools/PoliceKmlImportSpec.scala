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

package com.codeandmagic.ukgist.tools

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import com.codeandmagic.ukgist.model._
import java.util.NoSuchElementException
import java.io._
import com.codeandmagic.ukgist.util.InvalidKmlException
import com.codeandmagic.ukgist.model.Interval.FOREVER
import com.codeandmagic.ukgist.dao.{PoliceAreaDaoComponent, PoliceAreaDao}
import java.util

/**
 * User: cvrabie
 * Date: 28/03/2013
 */
class PoliceKmlImportSpec extends Specification with Mockito{
  import PoliceKmlImportFixture._
  import com.codeandmagic.ukgist.model.PolygonAreaFixture.beAPolygonArea
  import com.codeandmagic.ukgist.model.PolygonAreaFixture.LONDON_1_KML_OUTER

  "PoliceKmlImport.SOURCE" should{
    "correctly decode valid --kind parameters" in{
      tool(FLAG_SOURCE, FLAG_SOURCE_OTHER, PATH_DIR).SOURCE must beEqualTo(FLAG_SOURCE_OTHER_EXPECTED)
      tool(FLAG_SOURCE, FLAG_SOURCE_POLICE, PATH_DIR).SOURCE must beEqualTo(FLAG_SOURCE_POLICE_EXPECTED)
    }

    "provide POLICE as default if --kind argument is not present" in{
      tool(PATH_DIR).SOURCE must beEqualTo(FLAG_SOURCE_POLICE_EXPECTED)
    }

    "throw NoSuchElementException if an incorrect --kind is provided" in{
      tool(FLAG_SOURCE,FLAG_SOURCE_INVALID, PATH_DIR) must throwA(manifest[NoSuchElementException])
    }

    "throw InvalidArgumentException if the --kind parameter is missing" in{
      tool(FLAG_SOURCE, PATH_DIR) must throwA(manifest[IllegalArgumentException])
    }
  }

  "PoliceKmlImport.CLEAR" should{
    "correctly read the --clear argument" in{
      tool(FLAG_CLEAR, PATH_DIR).CLEAR must beTrue
    }

    "provide FALSE as default if the --clear argument is not present" in{
      tool(FLAG_SOURCE,FLAG_SOURCE_OTHER, PATH_DIR).CLEAR must beFalse
    }
  }

  "PoliceKmlImport.ONE" should{
    "correctly read the --one argument" in{
      tool(FLAG_ONE, PATH_KML).ONE must beTrue
    }

    "correctly read the --many argument" in{
      tool(FLAG_MANY, PATH_DIR).ONE must beFalse
    }

    "provide --many as default if neither --one  nor --many are present" in{
      tool(PATH_DIR).ONE must beFalse
    }

    "be confused if both --one and --many are present" in{
      tool(FLAG_ONE, FLAG_MANY, PATH_DIR) must throwA(manifest[IllegalArgumentException])
    }
  }

  "PoliceKmlImport.PREFIX" should{
    "correctly read the --prefix argument" in{
      tool(FLAG_PREFIX, FLAG_PREFIX_VAL, PATH_DIR).PREFIX must beEqualTo(FLAG_PREFIX_VAL)
    }

    "throw InvalidArgumentException if the --prefix parameter is missing" in{
      tool(FLAG_PREFIX, PATH_DIR) must throwA(manifest[IllegalArgumentException])
    }

    "provide empty string as default prefix if the --prefix flag is missing" in {
      tool(PATH_DIR).PREFIX must_== ""
    }
  }

  "PoliceKmlImport.VALID" should{
    "correctly read the --valid argument" in{
      tool(FLAG_VALID, FLAG_VALID_STR, PATH_DIR).VALIDITY must beEqualTo(FLAG_VALID_VAL)
    }
    "throw InvalidArgumentException if the --valid parameter is missing" in{
      tool(FLAG_VALID, PATH_DIR) must throwA(manifest[IllegalArgumentException])
    }
    "throw InvalidArgumentException if the --valid parameter is incorrect" in{
      tool(FLAG_VALID, FLAG_VALID_WRONG, PATH_DIR) must throwA(manifest[IllegalArgumentException])
    }
    "provide FOREVER as default validity if the --valid flag is missing" in{
      tool(PATH_DIR).VALIDITY must_== FOREVER
    }
  }

  "PoliceKmlImport.PATH" should{
    "correctly read the kml file if the --one flag is present" in{
      tool(FLAG_ONE,PATH_KML).PATH must beAFile.and(exist)
    }

    "correctly read the kml folder if the --many flag is present" in{
      tool(FLAG_MANY,PATH_DIR).PATH must beADirectory.and(exist)
    }

    "throw IllegalArgumentException if no PATH is given but there are more than one parameters" in{
      tool(FLAG_ONE) must throwA[IllegalArgumentException]
    }

    "throw IllegalArgumentException if --one is present and PATH is a folder" in{
      tool(FLAG_ONE,PATH_DIR) must throwA[IllegalArgumentException]
    }

    "throw IllegalArgumentException if --many is present and PATH is a file" in{
      tool(PATH_KML) must throwA[IllegalArgumentException]
    }

    "throw IllegalArgumentException if --one is present and PATH is not a KML file" in{
      tool(FLAG_ONE,PATH_KMZ) must throwA[IllegalArgumentException]
    }
  }

  "PoliceKmlImport.clear()" should{
    "ask for permission before clearing the database" in{
      val t = tool(FLAG_ONE,FLAG_CLEAR,PATH_KML)
      t.clear()
      val expected = t.MSG_CLEAR_QUESTION+"\n\n"+(t.MSG_CLEAR_START.format(Area.Source.POLICE.toString))+"\n"
      t.OUTPUT.toString must beEqualTo(expected)
      there was one(t.dao).deleteBySource(any)
    }

    "abort if the permission for clearing the database is not given" in{
      val t = tool(FLAG_ONE,FLAG_CLEAR,PATH_KML)(new ByteArrayInputStream("n".getBytes))
      t.clear() must throwA(manifest[RuntimeException])
      val expected = t.MSG_CLEAR_QUESTION+"\n\n"+t.MSG_CLEAR_SKIPPED+"\n"
      t.OUTPUT.toString must beEqualTo(expected)
      there was no(t.dao).deleteBySource(any)
    }
  }

  "PoliceKmlImport.readOne()" should{
    "correctly decode a PolygonArea from a valid KML" in{
      val t = tool(FLAG_ONE,PATH_KML)
      t.readOne()
      val area = t.QUEUE.take()
      area.name must beEqualTo(FILE_KML)
      area.source must beEqualTo(Area.Source.POLICE)
      area must beAPolygonArea(LONDON_1_KML_OUTER,Nil)
    }

    "throw InvalidKmlException exception if the passed file is broken" in {
      val t = tool(FLAG_ONE,BROKEN_KML_PATH)
      t.readOne() must throwA(manifest[InvalidKmlException])
    }
  }

  "PoliceKmlImport.readMany()" should{
    "recursively decode PolygonAreas from a folder with KMLs" in{
      val t = tool(PATH_DIR)
      t.readMany()
      val as = new util.ArrayList[PoliceArea]()
      t.QUEUE.drainTo(as)
      val areas = as.toArray(new Array[PoliceArea](0))
      areas.length must beEqualTo(3)
      val a1 = areas.find(_.name == "kmls-k1")
      a1 must beSome
      a1.get must beAPolygonArea(LONDON_1_KML_OUTER,Nil)
      val a2 = areas.find(_.name == "kmls-sub1-k2")
      a2 must beSome
      a2.get must beAPolygonArea(LONDON_1_KML_OUTER,Nil)
      val a3 = areas.find(_.name == "kmls-sub1-k3")
      a3 must beSome
      a3.get must beAPolygonArea(LONDON_1_KML_OUTER,Nil)
    }
  }
}

object PoliceKmlImportFixture{
  val FLAG_SOURCE = "--source"
  val FLAG_SOURCE_OTHER = "OTHER"
  val FLAG_SOURCE_OTHER_EXPECTED = Area.Source.OTHER
  val FLAG_SOURCE_POLICE = "POLICE"
  val FLAG_SOURCE_POLICE_EXPECTED = Area.Source.POLICE
  val FLAG_SOURCE_INVALID = "SOMEOTHER"
  val FLAG_CLEAR = "--clear"
  val FLAG_ONE = "--one"
  val FLAG_MANY = "--many"
  val FLAG_PREFIX = "--prefix"
  val FLAG_PREFIX_VAL = "abcd"
  val FLAG_VALID = "--valid"
  val FLAG_VALID_STR = "2012-12/2013-01"
  val FLAG_VALID_VAL = new Interval(new MonthInterval(2012,12), new MonthInterval(2013,1))
  val FLAG_VALID_WRONG = "2012-12-01/2013-20-01"
  val FILE_KML = "city-of-london-ce"
  val PATH_KML = "src/test/resources/"+FILE_KML+".kml"
  val BROKEN_KML_PATH = "src/test/resources/broken.kml"
  val PATH_KMZ = "src/test/resources/big.kmz"
  val PATH_DIR = "src/test/resources/kmls"

  implicit val in:InputStream = new ByteArrayInputStream("y".getBytes)
  def tool(args:String*)(implicit is:InputStream) =  new KmlImportMockRegistry(is, args:_*).policeKmlImportTool
}

class KmlImportMockRegistry(is:InputStream, args:String*) extends Mockito with PoliceKmlToolComponent with PoliceAreaDaoComponent{
  val policeAreaDao = mock[PoliceAreaDao]
  val policeKmlImportTool = new MockPoliceAreaTool

  class MockPoliceAreaTool extends PoliceKmlTool(args:_*){
    val OUTPUT = new ByteArrayOutputStream()
    val dao = policeAreaDao
    override val OUT = new PrintStream(OUTPUT)
    override val IN = is
    override val CONSUMER_TASK = new Runnable {
      def run() {}
    }
    override val SLEEP = 500
    override def apply():MockPoliceAreaTool = {
      super.apply()
      this
    }
  }
}
