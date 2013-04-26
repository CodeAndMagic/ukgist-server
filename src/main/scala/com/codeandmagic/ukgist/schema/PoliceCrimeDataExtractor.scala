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
import com.codeandmagic.ukgist.model.{Interval, PoliceCrimeData}
import org.joda.time.DateTime

/**
 * User: cvrabie
 * Date: 24/04/2013
 */
object PoliceCrimeDataExtractor extends RowExtractor[PoliceCrimeData]{
  def extract(row: Row) = new PoliceCrimeData(
    id = row.bigInt("id").get,
    information = InformationExtractor.extract(row),
    area = PoliceAreaExtractor.extract(row),
    allCrime = row.integer("all_crime").get,
    antiSocialBehaviour = row.integer("anti_social_behavior").get,
    burglary = row.integer("burglary").get,
    criminalDamage = row.integer("criminal_damage").get,
    drugs = row.integer("drugs").get,
    otherTheft = row.integer("other_theft").get,
    publicDisorder = row.integer("public_disorder").get,
    robbery = row.integer("robbery").get,
    shoplifting = row.integer("shoplifting").get,
    vehicleCrime = row.integer("vehicle_crime").get,
    violentCrime = row.integer("violent_crime").get,
    otherCrime = row.integer("other_crime").get
  )
}
