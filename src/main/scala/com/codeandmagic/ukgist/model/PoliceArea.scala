package com.codeandmagic.ukgist.model

import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.schema.{PoliceAreaExtractor, PoliceAreaSchemaTokens, ORBrokerHelper}
import org.orbroker.Transaction
import net.liftweb.common.Logger
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

  override def companion:Companion[_<:PoliceArea] = PoliceArea

  override def copyWithId(newId: Long):PoliceArea = new PoliceArea(newId, name, source, validity, kml, policeForce, neighborhood)
}

trait PoliceAreaDao extends AreaDao[PoliceArea] with Logger{
  def getById(id: Long) = ORBrokerHelper.broker.readOnly()(
    _.selectOne(PoliceAreaSchemaTokens.policeAreaGetById, "id"->id)
  )

  def listAll() = ORBrokerHelper.broker.readOnly()(
    _.selectAll(PoliceAreaSchemaTokens.policeAreaListAll)
  )

  def deleteByType(source: Area.Source.Value) = ORBrokerHelper.broker.transaction()(
    _.execute(PoliceAreaSchemaTokens.policeAreaDeleteBySource, "source"->source)
  )

  def saveAll(areas: Seq[PoliceArea]) = ORBrokerHelper.broker.transaction()(saveAll(areas,_))

  protected def saveAll(areas: Seq[PoliceArea], tx:Transaction):Seq[PoliceArea] = {
    val keys = mutable.ArrayBuffer[Long]()
    try{
      debug("Batch save %d PoliceAreas".format(areas.length))
      tx.executeBatchForKeys(PoliceAreaSchemaTokens.policeAreaSaveAll, ("area", areas)){ key:Long => keys += key }
      debug("Got back %d keys".format(keys.length))
      val saved = (areas, keys).zipped.map((a:PoliceArea, i:Long) => a.copyWithId(i))
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

object PoliceArea extends Persistent[PoliceArea] with PoliceAreaDao{
  val clazz = manifest[PoliceArea]
  def extractor = PoliceAreaExtractor
}