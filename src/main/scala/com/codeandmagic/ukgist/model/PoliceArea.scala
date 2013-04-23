package com.codeandmagic.ukgist.model

import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.schema.{PoliceAreaSchemaTokens, ORBrokerHelper}
import org.orbroker.Transactional
import net.liftweb.common.Logger
import com.codeandmagic.ukgist.util.withV
import scala.collection.mutable

/**
 * User: cvrabie
 * Date: 07/04/2013
 */
class PoliceArea(override val id:Long,
                 override val name:String,
                 override val source:Area.Source.Value,
                 override val validity:Interval,
                 override val kml:Kml,
                 val policeForce:String,
                 val neighborhood:String)
  extends KmlPolygonArea(id,name,source,validity,kml){
  override def copyWithId(newId: Long):PoliceArea = new PoliceArea(newId, name, source, validity, kml, policeForce, neighborhood)
}

object PoliceArea extends AreaDao[PoliceArea] with Logger{
  def getById(id: Long) = ORBrokerHelper.broker.readOnly()(
    _.selectOne(PoliceAreaSchemaTokens.policeAreaGetById, "id"->id)
  )

  def listAll() = ORBrokerHelper.broker.readOnly()(
    _.selectAll(PoliceAreaSchemaTokens.policeAreaListAll)
  )

  def deleteByType(t: Area.Source.Value) = ORBrokerHelper.broker.transactional()(
    _.execute(PoliceAreaSchemaTokens.policeAreaDeleteByType, "t"->t)
  )

  def saveAll(areas: Seq[PoliceArea]) = ORBrokerHelper.broker.transactional()(saveAll(areas,_))

  protected def saveAll(areas: Seq[PoliceArea], tx:Transactional):Seq[PoliceArea] = {
    val keys = mutable.ArrayBuffer[Long]()
    val rollback = tx.makeSavepoint()
    var ok = true
    try{
      debug("Batch save %d PoliceAreas".format(areas.length))
      tx.executeBatchForKeys(PoliceAreaSchemaTokens.policeAreaSaveAll, ("area", areas)){ key:Long => keys += key }
      debug("Got back %d keys".format(keys.length))
      val saved = (areas, keys).zipped.map((a:PoliceArea, i:Long) => a.copyWithId(i))
      saved.toSeq
    }catch {
      case e => {
        error("Error saving batch of PoliceAreas. Cause: ",e)
        ok = false
        Seq.empty
      }
    }finally {
      if(ok) withV(tx)(_=>debug("Commiting...")).commit()
      else withV(tx)(_=>debug("Rolling back..")).rollbackSavepoint(rollback)
    }
  }
}


