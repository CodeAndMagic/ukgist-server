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

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.orbroker.{Token, Transaction}
import com.codeandmagic.ukgist.model.Interval.FOREVER
import net.liftweb.common.Loggable
import com.codeandmagic.ukgist.dao.{BrokerComponent, BrokerPoliceAreaDaoComponent}

/**
 * User: cvrabie
 * Date: 24/04/2013
 */
class PoliceAreaSpec extends Specification with Mockito with Loggable{
  import PoliceAreaFixture._

  "PoliceAreaDao.saveAll" should{
    "assign IDs to all saved items" in{
      val saved = PA.sv(AREAS, tx)
      saved.length must_== AREA_KEYS.length
      saved(0).id must_== AREA_KEYS(0)
      saved(0).name must_== AREAS(0).name
      saved(1).id must_== AREA_KEYS(1)
      saved(1).name must_== AREAS(1).name
      //we'll have to assume that commit was called
    }
    "complain if a different number of items are saved than requested" in{
      //EXECUTE_BATCH_FOR_KEYS will return 2 keys while we requested to save 3 areas
      PA.sv( AREA1 :: AREAS, tx) must throwA(manifest[RuntimeException])
    }
  }
}

object PoliceAreaFixture extends Mockito with BrokerComponent with BrokerPoliceAreaDaoComponent{

  val broker = null
  val policeAreaDao = null

  val PA = new BrokerPoliceAreaDao {
    def sv(areas: Seq[PoliceArea], tx: Transaction) = super.saveAll(areas, tx)
  }

  val EXECUTE_BATCH_FOR_KEYS = (p1:Any,p2:Any) => {
    p1 match {
      case a:Array[_] if a.length > 0 && a.last.isInstanceOf[Function1[_,_]]  => {
        //last parameter should be the key handler function
        AREA_KEYS.foreach(a.last.asInstanceOf[(Long)=>Unit].apply(_))
      }
      case _ => throw new Exception("p1 is expected be the list of parameters passed to executeBatchForKeys")
    }
    AREAS.length
  }

  def tx = {
    val transaction = mock[Transaction]
    transaction.executeBatchForKeys(any[Token[Long]],any[(String,Traversable[_])],any[Seq[(String,Any)]]:_*)(any[Function1[Long,Unit]]) answers( EXECUTE_BATCH_FOR_KEYS)
    transaction
  }

  val AREA1 = new PoliceArea(-1, "a1", Area.Source.POLICE, FOREVER, PolygonAreaFixture.LONDON_1_KML, "f1", "n1")
  val AREA2 = new PoliceArea(-1, "a2", Area.Source.POLICE, FOREVER, PolygonAreaFixture.LONDON_1_KML, "f2", "n2")
  val AREAS = AREA1 :: AREA2 :: Nil
  val AREA_KEYS = 1L :: 2L :: Nil
}