package com.codeandmagic.ukgist.model

import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.schema.PoliceAreaExtractor
import net.liftweb.json.JsonAST.{JString, JField}

/**
 * User: cvrabie
 * Date: 07/04/2013
 */
class PoliceArea(override val id:Int,
                 override val name:String,
                 override val source:Area.Source.Value,
                 override val validity:Interval,
                 override val kml:Kml,
                 val policeForce:String,
                 val policeNeighborhood:String)
  extends KmlPolygonArea(id,name,source,validity,kml){

  override def companion:Companion[_<:PoliceArea] = PoliceArea

  override def copyWithId(newId: Int):PoliceArea = new PoliceArea(newId, name, source, validity, kml, policeForce, policeNeighborhood)

  override protected def fields = super.fields ++ List(
    JField("policeForce",JString(policeForce)),
    JField("policeNeighborhood",JString(policeNeighborhood))
  )
}


object PoliceArea extends Persistent[PoliceArea]{
  val clazz = manifest[PoliceArea]
  def extractor = PoliceAreaExtractor
}