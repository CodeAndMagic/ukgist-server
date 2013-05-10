package com.codeandmagic.ukgist.dao

import com.codeandmagic.ukgist.model._
import com.codeandmagic.ukgist.schema.InformationSchemaTokens
import net.liftweb.common.Logger
import scala.collection.mutable
import org.orbroker.Transaction
import java.sql.Connection

/**
 * User: cvrabie
 * Date: 28/04/2013
 */
trait InformationDao {
  def listAllInAreas(areas:Seq[_<:Area]):Seq[Information]
  def saveAll(data: Seq[Information], transaction: Transaction):Seq[Information]
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

    def saveAll(info: Seq[Information], tx: Transaction) =
      Dao.saveAll(classOf[Information], tx, InformationSchemaTokens.informationSaveAll, "info", info)(this)
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
  def deleteAll():Int
  def saveAll(data:Seq[T]):Seq[T]
}

trait PoliceCrimeDataDao extends InformationExtensionDao[PoliceCrimeData] {
  def getByInfoId(id: Int):Option[PoliceCrimeData]
  def saveAll(data: Seq[PoliceCrimeData]): Seq[PoliceCrimeData]
}

trait PoliceCrimeDataDaoComponent{
  val policeCrimeDataDao:PoliceCrimeDataDao
}

trait BrokerPoliceCrimeDataDaoComponent extends PoliceCrimeDataDaoComponent{
  this:BrokerComponent with InformationDaoComponent =>

  class BrokerPoliceCrimeDataDao extends PoliceCrimeDataDao with Logger{
    def getByInfoId(id: Int) = broker.readOnly()(
      _.selectOne(InformationSchemaTokens.policeCrimeDataGetById, "id"->id)
    )

    def deleteAll() = broker.transaction()(
      _.execute(InformationSchemaTokens.informationDeleteByDiscriminator, "discriminator"->PoliceCrimeData.discriminator)
    )

    def saveAll(data: Seq[PoliceCrimeData]) = broker.transaction()(saveAll(data,_))

    //cascade save. first all information then all crime data
    def saveAll(data: Seq[PoliceCrimeData], tx:Transaction) =
      saveAllCrimeData(saveAllInformation(data,tx),tx)

    //TODO we create way too many temporary objects in this method!!!
    //TODO we save a new information object even if it already has an id. probably should use a REPLACE
    protected def saveAllInformation(data:Seq[PoliceCrimeData], tx:Transaction) = {
      val infoToSave = data.map(_.information)
      val savedInfo = informationDao.saveAll(infoToSave,tx)
      (data,savedInfo).zipped.map({
        case (d:PoliceCrimeData, i:Information) => d.copyWithInformation(i)
      }).toSeq
    }

    protected def saveAllCrimeData(data: Seq[PoliceCrimeData], tx:Transaction) =
      Dao.saveAll(classOf[PoliceCrimeData],tx, InformationSchemaTokens.policeCrimeDataSaveAll, "data", data)(this)
  }
}