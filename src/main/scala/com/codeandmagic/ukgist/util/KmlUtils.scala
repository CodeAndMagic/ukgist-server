package com.codeandmagic.ukgist.util

import com.vividsolutions.jts.geom.{LinearRing, Polygon, Coordinate}
import de.micromata.opengis.kml.v_2_2_0.{Kml, Document, Placemark}
import de.micromata.opengis.kml.v_2_2_0.{Polygon => KmlPolygon, LinearRing => KmlLinearRing, Coordinate => KmlCoordinate}
import scala.collection.JavaConversions.asScalaBuffer
import GeometryUtils._

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
case class InvalidKmlException(msg:String, cause:Throwable) extends Exception(msg){
  def this(msg:String) = this(msg,null)
}

object KmlUtils{
  def kmlLinearRingToJtsLinearRing(ring: KmlLinearRing):LinearRing = new LinearRing(
    //our convention is x = longitude, y = latitude
    coordinateFactory.create(ring.getCoordinates.map(c => new Coordinate(c.getLongitude, c.getLatitude)).toArray),
    geometryFactory
  )

  def kmlPolygonToJtsPolygon(poly: KmlPolygon):Polygon = new Polygon(
    kmlLinearRingToJtsLinearRing(poly.getOuterBoundaryIs.getLinearRing),
    poly.getInnerBoundaryIs.map( boundary => kmlLinearRingToJtsLinearRing(boundary.getLinearRing) ).toArray,
    geometryFactory
  )

  @throws(classOf[InvalidKmlException])
  //@throws[InvalidKmlException]("if the KML is not of type root.Document.Placemark.Polygon")
  private def ifPolygon[T](kml: Kml)(f:(KmlPolygon)=>T):T = kml.getFeature match {
    /* TODO: this only accepts a very specific KML: must have a Document base feature with only one Placemark child */
    case document:Document => document.getFeature.get(0) match {
      case placemark:Placemark => placemark.getGeometry match {
        case poly:KmlPolygon => f(poly)
        case _ => throw new InvalidKmlException("Only KMLs with a Polygon geometry are accepted.")
      }
      case _ => throw new InvalidKmlException("Only KMLs with a Placemark as the fist child are accepted.")
    }
    case _ => throw new InvalidKmlException("Only KMLs with a Document root feature are accepted.")
  }

  def kmlPolygonToCoordinatesSeq(kml: Kml):Seq[KmlCoordinate] = ifPolygon(kml)(
    _.getOuterBoundaryIs.getLinearRing.getCoordinates.toSeq
  )
  def kmlPolygonToJtsPolygon(kml: Kml):Polygon = ifPolygon(kml)(kmlPolygonToJtsPolygon(_))
}
