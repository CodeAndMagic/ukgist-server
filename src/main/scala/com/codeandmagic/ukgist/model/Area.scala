package com.codeandmagic.ukgist.model

import de.micromata.opengis.kml.v_2_2_0.{Polygon => KmlPolygon, LinearRing => KmlLinearRing, Coordinate => KmlCoordinate, Placemark, Kml}
import com.codeandmagic.ukgist.schema._
import scala.collection.JavaConversions.asScalaBuffer

//converts Java List to Scala Buffer
import scala.math.{min,max}

/**
 * User: cvrabie1
 * Date: 23/03/2013
 */
trait Area {
  val id:Long
  val name:String

  /**
   * Checks if the passed location is within the bounding box of this area.
   * This operation needs to be very fast because is used to quickly search through multiple areas.
   * Use {@link Area#containsDefinitely}  if you want to be sure that this area does contain the location.
   * @param loc
   * @return TRUE if there's a change that this area contains loc. FALSE if definitely loc is not within this area.
   */
  def containsMaybe(loc:Location):Boolean

  /**
   * Checked if the passed location is within this area.
   * This operation is usually slow. {@link Area#containsMaybe} can be used before to quickly eliminate if the location
   * is not in this area.
   * @param loc
   * @return TRUE is this area contains loc. FALSE otherwise.
   */
  def containsDefinitely(loc:Location):Boolean
}

class KmlArea(
  override val id:Long,
  override val name:String,
  val kml:Option[Kml]
)
extends Area{

  /**
   * List of coordinates on the outer border of this kml if its geometry is a polygon
   */
  lazy private val coordinates:Seq[KmlCoordinate] = kml.flatMap( KmlArea.kmlPolygonToCoordinatesSeq(_) ).getOrElse(Nil)

  /**
   * Latitude and Longitude MIN and MAX values. Basically the bounding box for this area.
   * Used for quick calculations, like checking if the area DOES NOT contain a Location.
   */
  lazy private val (north,south,west,east) = KmlArea.getBoundingBox(coordinates)

  /**
   * If this area has an kml with a polygon geometry this will hold the geometry as a JST polygon.
   * This is used to perform precise calculations on the geometry, like checking if the area contains a Location.
   */
  lazy private val geometry = kml.flatMap(KmlArea.kmlPolygonToGeometry(_))


  def containsMaybe(loc: Location) = (loc.latlng.getLatitude, loc.latlng.getLongitude) match {
    case (lat,lng) => lat >= south && lat <= north && lng >= west && lng <= east
  }

  def containsDefinitely(loc: Location) = geometry.map(
    KmlArea.geometryContainsLocation(_,loc)
  ).getOrElse(false)
}

trait KmlAreaDao{
  def getById(id:Long):Option[KmlArea]
}

trait GeometryUtils{
  import com.vividsolutions.jts.geom.{GeometryFactory, LinearRing, Polygon, Coordinate, Geometry, Point}
  import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory

  val coordinateFactory = CoordinateArraySequenceFactory.instance()
  val geometryFactory = new GeometryFactory(coordinateFactory)

  def kmlLinearRingToJtsLinearRing(ring: KmlLinearRing):LinearRing = new LinearRing(
    coordinateFactory.create(ring.getCoordinates.map(c => new Coordinate(c.getLatitude, c.getLatitude)).toArray),
    geometryFactory
  )

  def kmlPolygonToJtsPolygon(poly: KmlPolygon):Polygon = new Polygon(
    kmlLinearRingToJtsLinearRing(poly.getOuterBoundaryIs.getLinearRing),
    poly.getInnerBoundaryIs.map( boundary => kmlLinearRingToJtsLinearRing(boundary.getLinearRing) ).toArray,
    geometryFactory
  )

  private def ifPolygon[T](kml: Kml)(f:(KmlPolygon)=>T):Option[T] = kml.getFeature match {
    case placemark:Placemark => placemark.getGeometry match {
      case poly:KmlPolygon => Some(f(poly))
      case _ => None
    }
    case _ =>None
  }

  def kmlPolygonToCoordinatesSeq(kml: Kml):Option[Seq[KmlCoordinate]] = ifPolygon(kml)(_.getOuterBoundaryIs.getLinearRing.getCoordinates.toSeq)
  def kmlPolygonToGeometry(kml: Kml):Option[Geometry] = ifPolygon(kml)(kmlPolygonToJtsPolygon(_))

  def geometryContainsLocation(geo:Geometry, l:Location):Boolean = geo.intersects(
    new Point(coordinateFactory.create(Array(new Coordinate(l.latlng.getLatitude, l.latlng.getLongitude))), geometryFactory)
  )

  def getBoundingBox(coordinates:Seq[KmlCoordinate]):(Double,Double,Double,Double) =
    coordinates.foldLeft((-180d,180d,180d,-180d))((tmp,loc)=>tmp match {
      case (n,s,w,e) => ( max(n,loc.getLatitude), min(s,loc.getLatitude), min(w,loc.getLongitude), max(e,loc.getLongitude) )
    })
}

object KmlArea extends KmlAreaDao with GeometryUtils{

  def getById(id: Long) = ORBrokerHelper.broker.readOnly()(
    _.selectOne(KmlAreaSchemaTokens.getById, "id"->id)
  )


}
