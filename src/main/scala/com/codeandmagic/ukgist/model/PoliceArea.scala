package com.codeandmagic.ukgist.model

import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.schema.PoliceAreaExtractor

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


object PoliceArea extends Persistent[PoliceArea]{
  val clazz = manifest[PoliceArea]
  def extractor = PoliceAreaExtractor
}