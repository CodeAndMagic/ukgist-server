package com.codeandmagic.ukgist.schema

import net.liftweb.util.Props
import org.orbroker.{QuerySession, Transactional, Broker}
import org.orbroker.config.{FileSystemRegistrant, BrokerConfig, SimpleDataSource}
import java.io.{PrintWriter, File}
import net.liftweb.common._
import net.liftweb.util.Helpers._
import com.codeandmagic.ukgist.util.{LogLevel, LogPrintWriter}

/**
 * User: cvrabie1
 * Date: 02/08/2012
 */
private[schema] object ORBrokerFactory{
  def apply() = (for{
    dbDriver <- Props.get("db.driver") ?~! "Need the db.driver property"
    dbUrl <- Props.get("db.url") ?~! "Need the db.url property"
    dbUser <- Props.get("db.user") ?~! "Need the db.user property"
    dbPass <- Props.get("db.password") ?~! "Need the db.password property"
  }yield {
    val configFolder = new File(classOf[ORBrokerHelper].getResource("/sql").toURI)
    val dataSource = new SimpleDataSource(dbUrl, dbDriver)
    dataSource.setLogWriter(new PrintWriter(new LogPrintWriter(classOf[ORBrokerHelper],LogLevel.TRACE),true))
    val brokerConfig = new BrokerConfig(dataSource)
    brokerConfig.setUser(dbUser, dbPass)
    FileSystemRegistrant(configFolder).register(brokerConfig)
    brokerConfig.verify(KmlAreaSchemaTokens.idSet)
    Broker(brokerConfig)
  }) match {
    case Full(b) => b
    case f:Failure => throw new Exception(f.messageChain)
    case _ => throw new Exception("Could not initialize ORBroker")
  }
}

object ORBrokerHelper extends ORBrokerHelper(ORBrokerFactory()) with Loggable {
  def init() = {
    logger.info("Initializing ORBroker")
  }
}

class ORBrokerHelper(val broker: Broker) {
  /*val transactionWrapper = new LoanWrapper {
    def apply[T](f: => T) = {
      broker.transactional() {
        session =>
          var txOk = false
          ORBrokerHelper.session(session)
          try {
            val res = f
            txOk = true
            res
          } catch {
            case e => {
              txOk = false
              throw e
            }
          } finally {
            if (txOk) session.commit()
            else session.rollback()
          }
      }
    }
  }*/

  def transactional[T]( f: Transactional => T ) = tryo{
    broker.transactional()( transactional => {
      var ok = false
      try {
        val resp = f(transactional)
        ok = true
        resp
      } catch {
        case e => {
          ok = false
          throw e;
        }
      } finally {
        if (ok) transactional.commit()
        else transactional.rollback()
      }
    })
  }

  def readOnly[T]( f: QuerySession => T ) = tryo{
    broker.readOnly()(f)
  }
}
