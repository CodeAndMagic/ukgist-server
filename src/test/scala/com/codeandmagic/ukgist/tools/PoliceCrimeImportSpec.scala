package com.codeandmagic.ukgist.tools

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import com.codeandmagic.ukgist.dao.{PoliceAreaDao, PoliceAreaDaoComponent}
import com.codeandmagic.ukgist.model.{Interval, PoliceArea}
import com.codeandmagic.ukgist.model.Area.Source
import com.codeandmagic.ukgist.model.Interval.FOREVER
import org.joda.time.DateTime

/**
 * User: cvrabie
 * Date: 02/05/2013
 */
class PoliceCrimeImportSpec extends Specification{
  import PoliceCrimeImportFixture._
  "PoliceCrimeImport.ONE" should{
    "correctly read the --one argument" in{
      tool(FLAG_ONE, PATH_CSV).ONE must beTrue
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

  "PoliceCrimeImport.readOne(String)" should{
    "correctly deserialize a correct CSV line" in{
      val maybeCrime = instance.testReadOne(LINE_1)
      maybeCrime must beSome
      val crime = maybeCrime.get
      crime.allCrime must_==(LINE_1_ALL_CRIME)
      crime.antiSocialBehaviour must_==(LINE_1_ANTI_SOCIAL)
      crime.otherCrime must_==(LINE_1_OTHER)
    }
    "does not choke on the header line" in{
      instance.testReadOne(LINE_H) must beNone
    }
    "throws IllegalArgumentException if the line does not match the format" in{
      instance.testReadOne(LINE_BROKEN) must throwA(manifest[IllegalArgumentException])
    }
  }

  "PoliceCrimeImport.readOne()" should{
    val importer = tool(FLAG_ONE, FLAG_VALID, FLAG_VALID_STR, PATH_CSV)
    "correctly deserialize a correct CSV file" in{
      val data = importer.readOne()
      data.length must_==(4)
      val first = data.head
      first.id must_==(-1)
      first.allCrime must_==(LINE_1_ALL_CRIME)
      first.information.validity must_==(VALIDITY)
      first.information.id must_==(-1)
      val last = data.last
      last.allCrime must_!=(LINE_1_ALL_CRIME)
    }
  }

  "PoliceCrimeImport.readMany()" should{
    val importer = tool(FLAG_MANY, PATH_DIR)
    "recursively decode PoliceCrimeData from a folder with CSVs" in{
      val data = importer.readMany()
      data.length must_!=(4*3)
      val first = data.head
      first.id must_==(-1)
      first.allCrime must_==(LINE_1_ALL_CRIME)
      first.information.validity must_==(FOREVER)
    }
  }
}

object PoliceCrimeImportFixture extends Mockito{
  val FLAG_CLEAR = "--clear"
  val FLAG_ONE = "--one"
  val FLAG_MANY = "--many"
  val FLAG_VALID = "--valid"

  val FLAG_VALID_STR = "2012-12/2013-01"
  val VALIDITY = new Interval(new DateTime(2012,12,1,0,0),new DateTime(2013,2,1,0,0))

  val FILE_CSV = "city-of-london"
  val PATH_CSV = "src/test/resources/"+FILE_CSV+".csv"
  val BROKEN_CSV_PATH = "src/test/resources/broken.csv"
  val PATH_DIR = "src/test/resources/csvs"

  val LINE_1_ALL_CRIME = 178
  val LINE_1_ANTI_SOCIAL = 7
  val LINE_1_OTHER = 5
  val LINE_1_FORCE = "City of London Police"
  val LINE_1_HOOD = "cs"
  val LINE_1 = "2013-02,%s,%s,%d,%d,3,5,18,78,6,2,27,7,20,%d".format(LINE_1_FORCE,LINE_1_HOOD,LINE_1_ALL_CRIME,LINE_1_ANTI_SOCIAL,LINE_1_OTHER)
  val LINE_BROKEN = "2013-02,City of London Police,cs,178,7,3,5,18,abcd,6,2,27,7,20,5"
  val LINE_H = "Month,Force,Neighbourhood,All crime,Anti-social behaviour,Burglary,Criminal damage and arson,Drugs,Other theft,Public disorder and weapons,Robbery,Shoplifting,Vehicle crime,Violent crime,Other crime"

  import com.codeandmagic.ukgist.model.PolygonAreaFixture.LONDON_1_KML
  val AREA_1 = new PoliceArea(1,LINE_1_FORCE,Source.POLICE,FOREVER,LONDON_1_KML,LINE_1_FORCE,LINE_1_HOOD)
  val AREAS = AREA_1 :: Nil

  def tool(args:String*) = new CrimeImportMockRegistry(args:_*).policeCrimeImportTool
  val instance = tool(PATH_DIR)
}

class CrimeImportMockRegistry(args:String*) extends Mockito with PoliceCrimeImportToolComponent with PoliceAreaDaoComponent{
  import PoliceCrimeImportFixture._
  val policeAreaDao = mock[PoliceAreaDao]
  policeAreaDao.listAll().returns(AREAS)
  val policeCrimeImportTool = new PoliceCrimeImportTool(args:_*){
    def testReadOne(line: String) = super.readOne(line)
  }
}

