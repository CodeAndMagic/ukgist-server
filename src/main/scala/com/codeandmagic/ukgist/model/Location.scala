package com.codeandmagic.ukgist.model

import com.javadocmd.simplelatlng.LatLng
import net.liftweb.util.Helpers.tryo

/**
 * User: cvrabie1
 * Date: 11/03/2013
 */

class Location(val latlng:LatLng){
  def this(lat:Double, lng:Double) = this(new LatLng(lat,lng))
}

object Location {
  def unapply(str:String):Option[Location] = str.split(',') match {
    case Array(Dbl(lat),Dbl(lng)) => Some(new Location(lat,lng))
    case _ => None
  }
}
