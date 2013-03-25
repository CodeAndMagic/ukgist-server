package com.codeandmagic.ukgist

import model.{Location, KmlArea}
import org.specs2.mock.Mockito
import java.io.FileInputStream
import org.orbroker.Row
import org.specs2.matcher.{Expectable, Matcher}
import de.micromata.opengis.kml.v_2_2_0.{Placemark, Document, Polygon, Kml}
import scala.collection.JavaConversions.asScalaBuffer

/**
 * User: cvrabie
 * Date: 25/03/2013
 */
object KmlAreaFixture extends Mockito{
  val LONDON_1_KML_PATH = "src/test/resources/city-of-london-ce.kml"
  val LONDON_1_AREA_NAME = "City of London A1"
  val LONDON_1_KML_ROW = mockBrokerRow(1,LONDON_1_AREA_NAME,LONDON_1_KML_PATH)
  val LONDON_1_KML_OUTER = Seq[Double](-0.0822154911802,51.5126573371,0, -0.0835048005585,51.5120968661,0, -0.084408489194,51.5129410016,0, -0.0825027849091,51.5153550743,0, -0.081218693625,51.5150077014,0, -0.080922247124,51.5153160599,0, -0.0810293567543,51.5159473431,0, -0.0799441527761,51.5154119249,0, -0.0790610522212,51.5155556089,0, -0.0768767881341,51.5166175031,0, -0.0735880711874,51.5140506256,0, -0.0727582065132,51.5103743174,0, -0.0730087271123,51.5101032539,0, -0.0755343309863,51.5097406747,0, -0.0762282952065,51.5105641085,0, -0.0768827787197,51.5104841042,0, -0.0778229127308,51.510109138,0, -0.0790879136025,51.5090679937,0, -0.078721133864,51.5088387849,0, -0.0792385020124,51.5078958212,0, -0.0810571212822,51.5070537752,0, -0.0799585488642,51.5085087622,0, -0.0816912211601,51.5090500425,0, -0.0822913091399,51.5095151445,0, -0.0800903206665,51.5114061328,0, -0.0802492495142,51.5120939691,0, -0.0822154911802,51.5126573371,0)
  val LONDON_1_KML = Kml.unmarshal(strToIs(LONDON_1_KML_PATH))
  val LONDON_1_AREA = new KmlArea(1,LONDON_1_AREA_NAME,Some(LONDON_1_KML))
  val LONDON_1_LOCATION_INSIDE_CONVEX_PART = new Location(51.512979, -0.078002)
  val LONDON_1_LOCATION_INSIDE_CONCAVE_PART = new Location(51.509842, -0.075439)
  val LONDON_1_LOCATION_OUTSIDE_CONVEX_PART = new Location(51.515513, -0.074154)
  val LONDON_1_LOCATION_OUTSIDE_CONCAVE_PART = new Location(51.511710, -0.081626)
  val LONDON_1_LOCATION_OUTSIDE_BOX = new Location(51.514372, -0.070852)
  val BROKEN_KML_PATH = "src/test/resources/broken.kml"
  val BROKEN_ROW = mockBrokerRow(1,LONDON_1_AREA_NAME,BROKEN_KML_PATH)

  implicit def strToIs(path:String) = new FileInputStream(path)
  def mockBrokerRow(id:Long, name:String, is:FileInputStream) = {
    val row = mock[Row]
    row.bigInt(anyString) returns Some(id)
    row.string(anyString) returns Some(name)
    row.binaryStream(anyString) returns Some(is)
    /*return*/ row
  }

  class KmlPolygonMatcher(
                           expectedOuterCoordinates:Iterable[Double],
                           expectedInnerCoordinates:Iterable[Double]*
                           ) extends Matcher[Kml]{
    def apply[S <: Kml](t: Expectable[S]) = {

      val kml:Kml = t.value;
      val polygon:Polygon = kml.getFeature.asInstanceOf[Document].getFeature
        .get(0).asInstanceOf[Placemark].getGeometry.asInstanceOf[Polygon]

      val actualOuterCoordinates:Iterable[Double] = polygon.getOuterBoundaryIs.getLinearRing.getCoordinates
        .map(c=>Seq(c.getLatitude,c.getLongitude).reverse :+ c.getAltitude).flatten
      //don't forget to reverse coordinate doubles because KML stores pairs of (LNG,LAT) instead of (LAT,LNG)

      val actualInnerCoordinates:Iterable[Iterable[Double]] = polygon.getInnerBoundaryIs
        .map(hole=>hole.getLinearRing.getCoordinates
        .map(c=>Seq(c.getLatitude,c.getLongitude).reverse :+ c.getAltitude)).flatten
      //don't forget to reverse coordinate doubles because KML stores pairs of (LNG,LAT) instead of (LAT,LNG)

      result(
        expectedOuterCoordinates == actualOuterCoordinates && expectedInnerCoordinates == actualInnerCoordinates,
        t.description+" has the same bounding polygon",
        t.description+" does not match the bounding polygon",
        t
      )
    }
  }

  def beAPolygon(expectedOuterCoordinates:Iterable[Double], expectedInnerCoordinates:Iterable[Double]*) =
    new KmlPolygonMatcher(expectedOuterCoordinates, expectedInnerCoordinates:_*)
}
