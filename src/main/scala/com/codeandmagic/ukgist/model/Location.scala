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
