package com.codeandmagic.ukgist.tools

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import com.codeandmagic.ukgist.model.Area
import java.util.NoSuchElementException

/**
 * User: cvrabie
 * Date: 28/03/2013
 */
class PoliceKmlImportSpec extends Specification{
  import PoliceKmlImportFixture._

  "PoliceKmlImportSpec" should{
    "correctly decode valid Area.KIND arguments" in{
      new PoliceKmlTool(FLAG_KIND, FLAG_KIND_OTHER).KIND must beEqualTo(FLAG_KIND_OTHER_EXPECTED)
      new PoliceKmlTool(FLAG_KIND, FLAG_KIND_POLICE).KIND must beEqualTo(FLAG_KIND_POLICE_EXPECTED)
    }

    "provide POLICE as default --kind" in{
      new PoliceKmlTool().KIND must beEqualTo(FLAG_KIND_POLICE_EXPECTED)
    }

    "throw NoSuchElementException if an incorrect kind is provided" in{
      new PoliceKmlTool(FLAG_KIND,FLAG_KIND_INVALID) must throwA(manifest[NoSuchElementException])
    }

    "throw InvalidArgumentException if the --kind parameter is missing" in{
      new PoliceKmlTool(FLAG_KIND) must throwA(manifest[IllegalArgumentException])
    }

    "correctly read the --clear argument" in{
      new PoliceKmlTool(FLAG_CLEAR).CLEAR must beTrue
    }

    "provide FALSE as default if the --clear argument is not present" in{
      new PoliceKmlTool(FLAG_KIND,FLAG_KIND_OTHER).CLEAR must beFalse
    }

    "correctly read the --one argument" in{
      new PoliceKmlTool(FLAG_ONE).ONE must beTrue
    }

    "correctly read the --many argument" in{
      new PoliceKmlTool(FLAG_MANY).ONE must beFalse
    }

    "provide --many as default if neither --one  nor --many are present" in{
      new PoliceKmlTool().ONE must beFalse
    }

    "be confused if both --one and --many are present" in{
      new PoliceKmlTool(FLAG_ONE, FLAG_MANY) must throwA(manifest[IllegalArgumentException])
    }

    "correctly read the --prefix argument" in{
      new PoliceKmlTool(FLAG_PREFIX, FLAG_PREFIX_VAL).PREFIX must beEqualTo(FLAG_PREFIX_VAL)
    }

    "throw InvalidArgumentException if the --prefix parameter is missing" in{
      new PoliceKmlTool(FLAG_PREFIX) must throwA(manifest[IllegalArgumentException])
    }

    "provide empty string as default prefix if the --prefix flag is missing" in {
      new PoliceKmlTool().PREFIX must beEqualTo("")
    }
  }
}

object PoliceKmlImportFixture extends Mockito{
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
}
