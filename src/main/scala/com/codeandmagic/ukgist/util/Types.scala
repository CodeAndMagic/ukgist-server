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

package com.codeandmagic.ukgist.util

import net.liftweb.util.Helpers._

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
object Dbl{
  def unapply(str:String):Option[Double] = tryo{str.trim.toDouble}.toOption
}

object Dec{
  def unapply(str:String):Option[Int] = tryo{str.trim.toInt}.toOption
}
