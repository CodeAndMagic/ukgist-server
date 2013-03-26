package com.codeandmagic.ukgist

import model.{Location, KmlArea, AreaIndex}
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import de.micromata.opengis.kml.v_2_2_0.Kml
import java.io.File
import scala.math._
import net.liftweb.common.Loggable

/**
 * User: cvrabie
 * Date: 25/03/2013
 */
class AreaIndexSpec extends Specification with Loggable{
  import KmlAreaFixture._
  import AreaIndexFixture._

  //args(skipAll = true) //

  logger.info("Loading KMZ with 1000 KMLs")
  val startLoad = System.currentTimeMillis()
  val bigAreaIndex = new AreaIndex(BIG_AREAS)
  def dumbSearch(loc:Location) = bigAreaIndex.areas.filter(_.containsMaybe(loc)).filter(_.containsDefinitely(loc))
  logger.info("Finished loading KMZ in %s ms".format(System.currentTimeMillis()-startLoad))

  "AreaIndex(BIG.KMZ)" should{

    "should have one area that contains "+LONDON_1_LOCATION_INSIDE_CONVEX_PART in{
      val results = bigAreaIndex.query(LONDON_1_LOCATION_INSIDE_CONVEX_PART)
      results.size must beEqualTo(1)
      results(0).id must beEqualTo(LONDON_1_INDEX)
    }

    "should have one area that contains "+LONDON_1_LOCATION_INSIDE_CONCAVE_PART in{
      val results = bigAreaIndex.query(LONDON_1_LOCATION_INSIDE_CONCAVE_PART)
      results.size must beEqualTo(1)
      results(0).id must beEqualTo(LONDON_1_INDEX)
    }

    "should have one area that contains "+LONDON_1_LOCATION_OUTSIDE_CONCAVE_PART in{
      val results = bigAreaIndex.query(LONDON_1_LOCATION_OUTSIDE_CONCAVE_PART)
      results.size must beEqualTo(1)
      results(0).id must be_!=(LONDON_1_INDEX)
    }

    "should be faster than just iterating all areas " in{
      val locs = 0.to(SPEED_TEST_ITERATIONS).map( _ => new Location(latMin + random*(latMax-latMin), lngMin+random*(lngMax-lngMin)) )

      //test dumb search
      val startNormalSearch = System.currentTimeMillis()
      locs.foreach(dumbSearch(_))
      val durationNormalSearch = System.currentTimeMillis() - startNormalSearch
      logger.info("Run time for %d normal searches was %d ms".format(SPEED_TEST_ITERATIONS,durationNormalSearch))

      //test indexed search
      val startIndexedSearch = System.currentTimeMillis()
      locs.foreach(bigAreaIndex.query(_))
      val durationIndexedSearch = System.currentTimeMillis() - startIndexedSearch
      logger.info("Run time for %d indexed searches was %d ms\n".format(SPEED_TEST_ITERATIONS,durationIndexedSearch))

      durationNormalSearch must be_>(durationIndexedSearch)
    }
  }
}

object AreaIndexFixture extends Mockito{
  val BIG_KMZ_PATH = "src/test/resources/big.kmz"
  val BIG_KMZ = Kml.unmarshalFromKmz(new File(BIG_KMZ_PATH))
  val BIG_AREAS = BIG_KMZ.zipWithIndex.map( _ match {
    case (kml,i)=> new KmlArea(i,""+i,Some(kml))
  })
  assert(BIG_AREAS.find(_.kml.isEmpty).isEmpty)
  val LONDON_1_INDEX = 742
  val (latMin, lngMin, latMax, lngMax) = (50.828106, -4.441416, 58.315364, 0.896581)
  val SPEED_TEST_ITERATIONS = 100000
}
