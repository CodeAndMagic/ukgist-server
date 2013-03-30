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
import com.codeandmagic.ukgist.model.{PolygonArea, AreaDao, Area}
import java.util.NoSuchElementException
import java.io._

/**
 * User: cvrabie
 * Date: 28/03/2013
 */
class PoliceKmlImportSpec extends Specification{
  import PoliceKmlImportFixture._

  "PoliceKmlImportSpec" should{
    "correctly decode valid Area.KIND arguments" in{
      tool(FLAG_KIND, FLAG_KIND_OTHER, PATH_DIR).KIND must beEqualTo(FLAG_KIND_OTHER_EXPECTED)
      tool(FLAG_KIND, FLAG_KIND_POLICE, PATH_DIR).KIND must beEqualTo(FLAG_KIND_POLICE_EXPECTED)
    }

    "provide POLICE as default --kind" in{
      tool(PATH_DIR).KIND must beEqualTo(FLAG_KIND_POLICE_EXPECTED)
    }

    "throw NoSuchElementException if an incorrect kind is provided" in{
      tool(FLAG_KIND,FLAG_KIND_INVALID, PATH_DIR) must throwA(manifest[NoSuchElementException])
    }

    "throw InvalidArgumentException if the --kind parameter is missing" in{
      tool(FLAG_KIND, PATH_DIR) must throwA(manifest[IllegalArgumentException])
    }

    "correctly read the --clear argument" in{
      tool(FLAG_CLEAR, PATH_DIR).CLEAR must beTrue
    }

    "provide FALSE as default if the --clear argument is not present" in{
      tool(FLAG_KIND,FLAG_KIND_OTHER, PATH_DIR).CLEAR must beFalse
    }

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

    "correctly read the --prefix argument" in{
      tool(FLAG_PREFIX, FLAG_PREFIX_VAL, PATH_DIR).PREFIX must beEqualTo(FLAG_PREFIX_VAL)
    }

    "throw InvalidArgumentException if the --prefix parameter is missing" in{
      tool(FLAG_PREFIX, PATH_DIR) must throwA(manifest[IllegalArgumentException])
    }

    "provide empty string as default prefix if the --prefix flag is missing" in {
      tool(PATH_DIR).PREFIX must beEqualTo("")
    }

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

    "ask for permission before clearing the database" in{
      val t = tool(FLAG_ONE,FLAG_CLEAR,PATH_KML).apply()
      val expected = t.CLEAR_QUESTION+"\n\n"+(t.CLEAR_START.format(Area.Kind.POLICE.toString))+"\n"
      t.OUTPUT.toString must beEqualTo(expected)
      there was one(t.areaDao).deleteByType(any)
    }

    "abort if the permission for clearing the database is not given" in{
      val t = tool(FLAG_ONE,FLAG_CLEAR,PATH_KML)(new ByteArrayInputStream("n".getBytes))
      t.apply() must throwA(manifest[RuntimeException])
      val expected = t.CLEAR_QUESTION+"\n\n"+t.CLEAR_SKIPPED+"\n"
      t.OUTPUT.toString must beEqualTo(expected)
      there was no(t.areaDao).deleteByType(any)
    }
  }
}

object PoliceKmlImportFixture extends Mockito{
  implicit val in:InputStream = new ByteArrayInputStream("y".getBytes)
  def tool(args:String*)(implicit in:InputStream):MockPoliceKmlTool =
    new MockPoliceKmlTool(in,args:_*)

  class MockPoliceKmlTool(val in:InputStream, override val args:String*) extends PoliceKmlTool(args:_*){
    override val areaDao = mock[AreaDao[PolygonArea]]
    val OUTPUT = new ByteArrayOutputStream()
    override val OUT = new PrintStream(OUTPUT)
    override val IN =  in
    override def apply():MockPoliceKmlTool = {
      super.apply()
      this
    }
  }

  val FLAG_KIND = "--kind"
  val FLAG_KIND_OTHER = "OTHER"
  val FLAG_KIND_OTHER_EXPECTED = Area.Kind.OTHER
  val FLAG_KIND_POLICE = "POLICE"
  val FLAG_KIND_POLICE_EXPECTED = Area.Kind.POLICE
  val FLAG_KIND_INVALID = "SOMEOTHER"
  val FLAG_CLEAR = "--clear"
  val FLAG_ONE = "--one"
  val FLAG_MANY = "--many"
  val FLAG_PREFIX = "--prefix"
  val FLAG_PREFIX_VAL = "abcd"
  val PATH_KML = "src/test/resources/city-of-london-ce.kml"
  val PATH_KMZ = "src/test/resources/big.kmz"
  val PATH_DIR = "src/test/resources/"
}
