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

import org.orbroker.config.TokenSet
import org.orbroker.Token
import org.orbroker.conv.JodaDateTimeConv

/**
 * User: cvrabie
 * Date: 23/03/2013
 */

object PoliceAreaSchemaTokens extends TokenSet(true) {
  val policeAreaGetById = Token('policeAreaGetById, PoliceAreaExtractor)
  val policeAreaListBatch = Token('policeAreaListBatch, PoliceAreaExtractor)
  val policeAreaDeleteBySource = Token[Int]('policeAreaDeleteBySource, AreaSourceConv)
  val policeAreaSaveAll = Token[Int]('policeAreaSaveAll, JodaDateTimeConv, AreaSourceConv, KmlConv)
}

object InformationSchemaTokens extends TokenSet(true){
  val informationListAllInAreas = Token('informationListAllInAreas, InformationExtractor)
  val informationDeleteByDiscriminator = Token[Int]('informationDeleteByDiscriminator)
  val informationSaveAll = Token[Int]('informationSaveAll, JodaDateTimeConv)
  val policeCrimeDataGetById = Token('policeCrimeDataGetById, PoliceCrimeDataExtractor)
  val policeCrimeDataSaveAll = Token[Int]('policeCrimeDataSaveAll)
}