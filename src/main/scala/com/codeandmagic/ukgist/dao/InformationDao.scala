package com.codeandmagic.ukgist.dao

import com.codeandmagic.ukgist.model._
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

trait MergeInformationExtensionDao{
  def getDiscriminatorAndInformationId(discriminator: Class[_<:InformationExtension], informationId:Int):Option[InformationExtension]

}

trait MergeInformationExtensionDaoComponent{
  val informationExtensionDao:MergeInformationExtensionDao
}

trait DiscriminatorInformationExtensionDaoComponent extends MergeInformationExtensionDaoComponent{
  this:PoliceCrimeDataDaoComponent =>

  class DiscriminatorInformationExtensionDao extends MergeInformationExtensionDao with Logger{
    //TODO this is not scalable
    val TPoliceCrimeData = classOf[PoliceCrimeData]
    def getDiscriminatorAndInformationId(discriminator: Class[_<:InformationExtension], informationId: Int) = discriminator match {
      case TPoliceCrimeData => policeCrimeDataDao.getByInfoId(informationId)
      case _ => throw new ClassCastException(("Discriminator %s is unknown or not for an InformationExtension. " +
      "Is the discriminator stable and the class holding it loaded? Has the class name changed?").format(discriminator))
    }
  }
}

trait InformationExtensionDao[T <: InformationExtension] {
  def getByInfoId(id:Int):Option[T]
}

trait PoliceCrimeDataDao extends InformationExtensionDao[PoliceCrimeData]

trait PoliceCrimeDataDaoComponent{
  val policeCrimeDataDao:PoliceCrimeDataDao
}

trait BrokerPoliceCrimeDataDaoComponent extends PoliceCrimeDataDaoComponent{
  this:BrokerComponent =>

  import com.codeandmagic.ukgist.util.withV
  class BrokerPoliceCrimeDataDao extends PoliceCrimeDataDao with Logger{
    def getByInfoId(id: Int) = broker.readOnly()(
      _.selectOne(InformationSchemaTokens.policeCrimeDataGetById, "id"->id)
    )
  }
}