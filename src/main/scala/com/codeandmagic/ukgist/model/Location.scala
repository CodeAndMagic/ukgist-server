package com.codeandmagic.ukgist.model

import com.javadocmd.simplelatlng.LatLng
import com.codeandmagic.ukgist.util.Dbl

/**
 * User: cvrabie
 * Date: 11/03/2013
 */

class Location(val lat:Double, val lng:Double){
  val latLng = new LatLng(lat,lng)
  override def toString = "(%f,%f)".format(lat,lng)
}

object Location {
  def unapply(str:String):Option[Location] = str.split(',') match {
    case Array(Dbl(lat),Dbl(lng)) => Some(new Location(lat,lng))
    case _ => None
  }
}
