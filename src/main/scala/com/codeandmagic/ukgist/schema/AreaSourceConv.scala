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

package com.codeandmagic.ukgist.schema

import org.orbroker.conv.ParmConverter
import com.codeandmagic.ukgist.model.Area
import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.util.withV
import java.io.ByteArrayOutputStream

/**
 * User: cvrabie
 * Date: 23/04/2013
 */
object AreaSourceConv extends ParmConverter{
  type T = Area.Source.Value
  val fromType = classOf[T]
  def toJdbcType(t: T) = t.id
}

object KmlConv extends ParmConverter{
  type T = Kml
  val fromType = classOf[T]
  def toJdbcType(t: KmlConv.T) = withV(new ByteArrayOutputStream)(t.marshal(_)).toByteArray
}