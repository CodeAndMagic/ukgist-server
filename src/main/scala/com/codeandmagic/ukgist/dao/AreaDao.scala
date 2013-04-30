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
import scala.collection.mutable

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

    def listAll() = broker.readOnly()(
      _.selectAll(PoliceAreaSchemaTokens.policeAreaListAll)
    )

    def deleteBySource(source: Area.Source.Value) = broker.transaction()(
      _.execute(PoliceAreaSchemaTokens.policeAreaDeleteBySource, "source"->source)
    )

    def saveAll(areas: Seq[PoliceArea]) = broker.transaction()(saveAll(areas,_))

    protected def saveAll(areas: Seq[PoliceArea], tx:Transaction):Seq[PoliceArea] = {
      val keys = mutable.ArrayBuffer[Int]()
      try{
        debug("Batch save %d PoliceAreas".format(areas.length))
        //UGLY HACK BECAUSE MYSQL RETURN LONG GENERATED KEY FOR INT COLUMN
        //@see http://dev.mysql.com/doc/refman/5.5/en/information-functions.html#function_last-insert-id
        val func = ((x:Number)=> keys += x.intValue()).asInstanceOf[Int=>Unit]
        tx.executeBatchForKeys(PoliceAreaSchemaTokens.policeAreaSaveAll, ("area", areas))(func)
        debug("Got back %d keys".format(keys.length))
        val saved = (areas, keys).zipped.map((a:PoliceArea, i:Int) => a.copyWithId(i))
        if(saved.size != areas.size) throw new RuntimeException("Sent %d but saved %d".format(areas.size,saved.size))
        saved.toSeq
      }catch {
        case e => {
          error("Error saving batch of PoliceAreas. Cause: ",e)
          throw e //ORBroker should rollback at this point
        }
      }
    }
  }

}
