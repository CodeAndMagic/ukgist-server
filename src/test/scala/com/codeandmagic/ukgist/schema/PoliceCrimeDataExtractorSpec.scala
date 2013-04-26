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

import org.specs2.mock.Mockito
import org.orbroker.Row
import com.codeandmagic.ukgist.model.{PoliceCrimeData, KmlPolygonArea}
import java.sql.Timestamp

/**
 * User: cvrabie
 * Date: 26/04/2013
 */
class PoliceCrimeDataExtractorSpec {

}

object PoliceCrimeDataExtractorFixture extends Mockito{
  val CRIME_1_ID = 2
  val CRIME_1_ROW = {
    val row = mock[Row]
    row.bigInt("id") returns Some(CRIME_1_ID)
    row.integer("discriminator") returns Some(PoliceCrimeData.discriminator)
    //TODO rest of the fields
    /* return */ row
  }
}
