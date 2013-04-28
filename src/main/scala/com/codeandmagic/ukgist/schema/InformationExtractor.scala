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

import org.orbroker.{Row, RowExtractor}
import com.codeandmagic.ukgist.model.{Interval, Information}
import org.joda.time.DateTime

/**
 * User: cvrabie
 * Date: 24/04/2013
 */
object InformationExtractor extends RowExtractor[Information]{
  def extract(row: Row) = new Information(
    row.bigInt("info_id").get,
    row.integer("info_discriminator").get,
    AreaExtractor.extract(row),
    new Interval(
      row.timestamp("info_validity_start").map(new DateTime(_)),
      row.timestamp("info_validity_end").map(new DateTime(_))
    )
  )
}
