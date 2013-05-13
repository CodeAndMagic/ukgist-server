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

package com.codeandmagic.ukgist.dao

import com.codeandmagic.ukgist.model.{PoliceArea, Area}
import net.liftweb.common.Logger
import com.codeandmagic.ukgist.schema.{PoliceAreaSchemaTokens}
import org.orbroker.Transaction
import scala.collection.immutable.Stream

/**
 * User: cvrabie
 * Date: 27/04/2013
 */
trait AreaDao[T<:Area]{
  /**
   * Deletes from the database all areas with the specified type.
   * @return the number of deleted items
   */
  def deleteBySource(t:Area.Source.Value):Int

  /**
   * Fetches from the database the area with the specified id.
   * @param id
   * @return
   */
  def getById(id:Long):Option[T]

  /**
   * Fetches all the areas from the database.
   * @return
   */
  def listAll():Seq[T]

  def saveAll(areas:Seq[T]):Seq[T]
}

trait PoliceAreaDao extends AreaDao[PoliceArea]

trait PoliceAreaDaoComponent{
  val policeAreaDao:PoliceAreaDao
}

trait BrokerPoliceAreaDaoComponent extends PoliceAreaDaoComponent{
  this:BrokerComponent => //cake pattern

  class BrokerPoliceAreaDao extends PoliceAreaDao with Logger{

    def getById(id: Long) = broker.readOnly()(
      _.selectOne(PoliceAreaSchemaTokens.policeAreaGetById, "id"->id)
    )

    def listBatch(limit:Int, offset:Int) = broker.readOnly()(
      _.selectAll(PoliceAreaSchemaTokens.policeAreaListBatch,"limit"->limit,"offset"->offset)
    )

    private def batchStream(limit:Int, offset:Int):Stream[IndexedSeq[PoliceArea]] = {
      val batch = listBatch(limit,offset)
      debug("BatchStream[%d,%d] got %d PoliceAreas".format(limit,offset,batch.size))
      batch
    } #:: batchStream(limit, offset+limit)

    val BATCH_SIZE = 100

    def listAll() = batchStream(BATCH_SIZE, 0).takeWhile(_.size>0).flatten

    def deleteBySource(source: Area.Source.Value) = broker.transaction()(
      _.execute(PoliceAreaSchemaTokens.policeAreaDeleteBySource, "source"->source)
    )

    def saveAll(areas: Seq[PoliceArea]) = broker.transaction()(saveAll(areas,_))

    protected def saveAll(areas: Seq[PoliceArea], tx:Transaction):Seq[PoliceArea] =
      Dao.saveAll(classOf[PoliceArea],tx,PoliceAreaSchemaTokens.policeAreaSaveAll,"area",areas)(this)
  }

}
