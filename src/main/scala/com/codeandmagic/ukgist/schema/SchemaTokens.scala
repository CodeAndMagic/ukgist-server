package com.codeandmagic.ukgist.schema

import org.orbroker.config.TokenSet
import org.orbroker.Token

/**
 * User: cvrabie
 * Date: 23/03/2013
 */

object KmlAreaSchemaTokens extends TokenSet(true) {
  val getById = Token('kmlAreaGetById, KmlPolygonAreaExtractor)
  val listAll = Token('kmlAreaListAll, KmlPolygonAreaExtractor)
  val deleteByType = Token[Int]('kmlAreaDeleteByType)
}