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
import java.sql.Timestamp
import org.joda.time.DateTime
import org.orbroker.Row

/**
 * User: cvrabie
 * Date: 27/04/2013
 */
class InformationSpec {

}

object InformationFixture extends Mockito{
  val INFO_1_ID = 2
  val INFO_1_VALIDITY_START = new Timestamp(new DateTime().getMillis)
  val INFO_1_VALIDITY_END = new Timestamp(new DateTime().plusMonths(1).getMillis)

  import PolygonAreaFixture._
  val INFO_1_ROW = LONDON_1_KML_ROW
  INFO_1_ROW.integer("info.id") returns INFO_1_ID.toOption
  INFO_1_ROW.integer("info.discriminator") returns 0.toOption
  INFO_1_ROW.timestamp("info.validity_start") returns INFO_1_VALIDITY_START.toOption
  INFO_1_ROW.timestamp("info.validity_end") returns INFO_1_VALIDITY_END.toOption
}
