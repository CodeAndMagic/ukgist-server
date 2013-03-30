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

package com.codeandmagic.ukgist.model

import org.specs2.mock.Mockito
import java.io.FileInputStream
import org.orbroker.Row
import org.specs2.matcher.{Matcher, Expectable}
import de.micromata.opengis.kml.v_2_2_0.{Placemark, Document, Polygon, Kml}
import scala.collection.JavaConversions.asScalaBuffer
import com.vividsolutions.jts.geom.{Polygon=>JstPolygon}
import com.vividsolutions.jts.geom

/**
 * User: cvrabie
 * Date: 25/03/2013
 */
object PolygonAreaFixture extends Mockito{
  val LONDON_1_KML_PATH = "src/test/resources/city-of-london-ce.kml"
  val LONDON_1_AREA_NAME = "City of London A1"
  val LONDON_1_AREA_KIND = Area.Kind.POLICE
  val LONDON_1_KML_ROW = mockBrokerRow(1,LONDON_1_AREA_NAME,LONDON_1_AREA_KIND,LONDON_1_KML_PATH)
  val LONDON_1_KML_OUTER = Seq[Double](-0.0822154911802,51.5126573371,0, -0.0835048005585,51.5120968661,0, -0.084408489194,51.5129410016,0, -0.0825027849091,51.5153550743,0, -0.081218693625,51.5150077014,0, -0.080922247124,51.5153160599,0, -0.0810293567543,51.5159473431,0, -0.0799441527761,51.5154119249,0, -0.0790610522212,51.5155556089,0, -0.0768767881341,51.5166175031,0, -0.0735880711874,51.5140506256,0, -0.0727582065132,51.5103743174,0, -0.0730087271123,51.5101032539,0, -0.0755343309863,51.5097406747,0, -0.0762282952065,51.5105641085,0, -0.0768827787197,51.5104841042,0, -0.0778229127308,51.510109138,0, -0.0790879136025,51.5090679937,0, -0.078721133864,51.5088387849,0, -0.0792385020124,51.5078958212,0, -0.0810571212822,51.5070537752,0, -0.0799585488642,51.5085087622,0, -0.0816912211601,51.5090500425,0, -0.0822913091399,51.5095151445,0, -0.0800903206665,51.5114061328,0, -0.0802492495142,51.5120939691,0, -0.0822154911802,51.5126573371,0)
  val LONDON_1_KML = Kml.unmarshal(strToIs(LONDON_1_KML_PATH))
  val LONDON_1_AREA = new KmlPolygonArea(1,LONDON_1_AREA_NAME,LONDON_1_AREA_KIND,LONDON_1_KML)
  val LONDON_1_LOCATION_INSIDE_CONVEX_PART = new Location(51.512979, -0.078002)
  val LONDON_1_LOCATION_INSIDE_CONCAVE_PART = new Location(51.509842, -0.075439)
  val LONDON_1_LOCATION_OUTSIDE_CONVEX_PART = new Location(51.515513, -0.074154)
  val LONDON_1_LOCATION_OUTSIDE_CONCAVE_PART = new Location(51.511710, -0.081626)
  val LONDON_1_LOCATION_OUTSIDE_BOX = new Location(51.514372, -0.070852)
  val BROKEN_KML_PATH = "src/test/resources/broken.kml"
  val BROKEN_ROW = mockBrokerRow(1,LONDON_1_AREA_NAME,LONDON_1_AREA_KIND,BROKEN_KML_PATH)

  implicit def strToIs(path:String) = new FileInputStream(path)
  def mockBrokerRow(id:Long, name:String, kind:Area.Kind.Value, is:FileInputStream) = {
    val row = mock[Row]
    row.bigInt(anyString) returns Some(id)
    row.string(anyString) returns Some(name)
    row.smallInt(anyString) returns Some(LONDON_1_AREA_KIND.id.toShort)
    row.binaryStream(anyString) returns Some(is)
    /*return*/ row
  }

  class KmlPolygonMatcher(
     expectedOuterCoordinates:Seq[Double],
     expectedInnerCoordinates:Seq[Double]*
   ) extends Matcher[Kml]{
    def apply[S <: Kml](t: Expectable[S]) = {

      val kml:Kml = t.value;
      val polygon:Polygon = kml.getFeature.asInstanceOf[Document].getFeature
        .get(0).asInstanceOf[Placemark].getGeometry.asInstanceOf[Polygon]

      val actualOuterCoordinates:Seq[Double] = polygon.getOuterBoundaryIs.getLinearRing.getCoordinates
        .map(c=>Seq(c.getLongitude,c.getLatitude,c.getAltitude)).flatten
      //don't forget to reverse coordinate doubles because KML stores pairs of (LNG,LAT) instead of (LAT,LNG)

      val actualInnerCoordinates:Seq[Double] = polygon.getInnerBoundaryIs
        .map(hole=>hole.getLinearRing.getCoordinates
          .map(c=>Seq(c.getLongitude,c.getLatitude,c.getAltitude)).flatten).flatten
      //don't forget to reverse coordinate doubles because KML stores pairs of (LNG,LAT) instead of (LAT,LNG)

      result(
        expectedOuterCoordinates == actualOuterCoordinates && expectedInnerCoordinates == actualInnerCoordinates,
        t.description+" has the same bounding polygon",
        t.description+" does not match the bounding polygon",
        t
      )
    }
  }

  object PolygonMatcher{
    def isAPoly(poly:JstPolygon, expectedOuter:Seq[Double], expectedInner:Seq[Seq[Double]]) = {
      val actualOuterCoordinates:Seq[Double] = poly.getExteriorRing.getCoordinates.map(c => Seq(c.x, c.y, 0:Double)).flatten.sortWith(_<_)
      val actualInnerCoordinates:Seq[Double] = 0.until(poly.getNumInteriorRing)
        .map(i=>poly.getInteriorRingN(i).getCoordinates
        .map(c=>Seq(c.x,c.y,0:Double)).flatten).flatten.sortWith(_<_)
      expectedOuter.sortWith(_<_) == actualOuterCoordinates && expectedInner.flatten.sortWith(_<_) == actualInnerCoordinates
    }
  }

  class PolygonMatcher( expectedOuterCoordinates:Seq[Double], expectedInnerCoordinates:Seq[Double]*)
  extends Matcher[JstPolygon]{
    def apply[S <: geom.Polygon](t: Expectable[S]) = result(
      PolygonMatcher.isAPoly(t.value,expectedOuterCoordinates, expectedInnerCoordinates),
      t.description+" has the same bounding polygon", t.description+" does not match the bounding polygon", t
    )
  }

  class PolygonAreaMatcher( expectedOuterCoordinates:Seq[Double], expectedInnerCoordinates:Seq[Double]*)
    extends Matcher[PolygonArea]{
    def apply[S <: PolygonArea](t: Expectable[S]) = result(
      PolygonMatcher.isAPoly(t.value.geometry,expectedOuterCoordinates, expectedInnerCoordinates),
      t.description+" has the same bounding polygon", t.description+" does not match the bounding polygon", t
    )
  }

  def beAPolygonArea(expectedOuterCoordinates:Seq[Double], expectedInnerCoordinates:Seq[Double]*) =
    new PolygonAreaMatcher(expectedOuterCoordinates, expectedInnerCoordinates:_*)

  def beAPolygon(expectedOuterCoordinates:Seq[Double], expectedInnerCoordinates:Seq[Double]*) =
    new PolygonMatcher(expectedOuterCoordinates, expectedInnerCoordinates:_*)

  def beAKmlPolygon(expectedOuterCoordinates:Seq[Double], expectedInnerCoordinates:Seq[Double]*) =
    new KmlPolygonMatcher(expectedOuterCoordinates, expectedInnerCoordinates:_*)
}
