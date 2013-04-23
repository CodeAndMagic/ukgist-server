package com.codeandmagic.ukgist.model

import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.schema.{PoliceAreaSchemaTokens, ORBrokerHelper}
import org.orbroker.Transactional

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

object PoliceArea extends AreaDao[PoliceArea]{
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
    val keys = scala.collection.mutable.Seq[Int]()
    val rollback = tx.makeSavepoint()
    var ok = true
    try{
      tx.executeBatchForKeys(PoliceAreaSchemaTokens.policeAreaSaveAll, ("area", areas)){ key:Int => keys :+ key }
      val saved = (areas, keys).zipped.map((a:PoliceArea, i:Int) => a.copyWithId(i))
      saved.toSeq
    }catch {
      case e =>
        ok = false
        Seq.empty
    }finally {
      if (ok && keys.length == areas.length) tx.commit()
      else tx.rollbackSavepoint(rollback)
    }
  }
}


