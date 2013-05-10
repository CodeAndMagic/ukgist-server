package com.codeandmagic.ukgist.dao

import com.codeandmagic.ukgist.model.{PoliceCrimeData, Entity}
import scala.collection.mutable
import com.codeandmagic.ukgist.schema.InformationSchemaTokens
import net.liftweb.common.Logger
import org.orbroker.{Token, Transaction}

/**
 * User: cvrabie
 * Date: 03/05/2013
 */
object Dao {
  def saveAll[T <: Entity](cls:Class[T], tx:Transaction, token:Token[Int], name:String, data:Seq[T])(implicit logger:Logger):Seq[T] = {
    val keys = mutable.ArrayBuffer[Int]()
    try{
      logger.debug("Batch save %d %s".format(data.length, cls.getSimpleName))
      //UGLY HACK BECAUSE MYSQL RETURN LONG GENERATED KEY FOR INT COLUMN
      //@see http://dev.mysql.com/doc/refman/5.5/en/information-functions.html#function_last-insert-id
      val func = ((x:Number)=> keys += x.intValue()).asInstanceOf[Int=>Unit]
      tx.executeBatchForKeys(token,(name,data))(func)
      logger.debug("Got back %d keys".format(keys.length))
      val saved = (data, keys).zipped.map((a:T, i:Int) => a.copyWithId(i).asInstanceOf[T])
      if(saved.size != data.size) throw new RuntimeException("Sent %d but saved %d".format(data.size,saved.size))
      saved.toSeq
    }catch {
      case e => {
        logger.error("Error saving batch of %s. Cause: ".format(cls.getSimpleName),e)
        throw e //ORBroker should rollback at this point
      }
    }
  }
}
