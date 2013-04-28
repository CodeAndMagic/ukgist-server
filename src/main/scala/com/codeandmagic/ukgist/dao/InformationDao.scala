package com.codeandmagic.ukgist.dao

import com.codeandmagic.ukgist.model.{Information, Area}
import com.codeandmagic.ukgist.schema.InformationSchemaTokens
import net.liftweb.common.Logger

/**
 * User: cvrabie
 * Date: 28/04/2013
 */
trait InformationDao {
  def listAllInAreas(areas:Seq[_<:Area]):Seq[Information]
}

trait InformationDaoComponent{
  val informationDao:InformationDao
}

trait BrokerInformationDaoComponent extends InformationDaoComponent{
  this:BrokerComponent =>

  class BrokerInformationDao extends InformationDao with Logger{
    //TODO THIS DOES NOT MATCH MORE THAN 10 AREAS. WE CAN'T JUST PASS AN ARRAY OF IDS TO AN IN SQL STATEMENT
    //@see http://stackoverflow.com/questions/178479/preparedstatement-in-clause-alternatives
    val params = IndexedSeq("a0","a1","a2","a3","a4","a5","a6","a7","a8","a9")
    def listAllInAreas(areas: Seq[_ <: Area]) = broker.readOnly()( broker => {
      if(areas.size>params.size) error("QUERY FOR MORE THAN 10 AREA. SOME RESULTS WILL BE LOST!")
      val ids = params.zip(areas.map(_.id))
      broker.selectAll(InformationSchemaTokens.informationListAllInAreas, ids:_*)
    })
  }
}