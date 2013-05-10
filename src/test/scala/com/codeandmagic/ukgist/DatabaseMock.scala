package com.codeandmagic.ukgist

import com.googlecode.flyway.core.Flyway
import com.codeandmagic.ukgist.dao.{BrokerComponent, ORBrokerFactory}
import org.specs2.mutable.Specification
import org.specs2.specification.{Step, Fragments}
import net.liftweb.common.Logger
import java.io.File

/**
 * User: cvrabie
 * Date: 07/05/2013
 */
class DatabaseMock(val name:String, folders:String*) extends Logger{
  import DatabaseMock._

  override def toString = "MockDatabase[%s]".format(name)

  info("Constructing %s".format(this))

  val url = DatabaseMock.dbUrl.format(name)

  val broker = ORBrokerFactory.apply(dbDriver, url, dbUser, dbPass)

  val flyway = new Flyway

  private def abs(path:String) = Thread.currentThread().getContextClassLoader().getResource(path)

  val locations =  ("db" +: folders)

  locations.foreach(l=>debug("%s: loading migration information from '%s'".format(this,l)))

  def clean(){
    debug("Cleaning %s".format(this))
    flyway.setDataSource(broker.dataSource)
    flyway.clean()
    flyway.init()
    flyway.setLocations(locations:_*)
    flyway.migrate()
  }

  clean()
  debug("%s is READY!".format(this))
}

object DatabaseMock {
  private val dbDriver = "org.h2.Driver"
  private val dbUrl = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1"
  private val dbUser = ""
  private val dbPass =  ""

  def apply(name:String, migrationFolders:String*) = new DatabaseMock(name,migrationFolders:_*)
}

class MockBrokerComponent(val name:String, val migrationLocations:String*) extends BrokerComponent{
  val db = DatabaseMock(name, migrationLocations:_*)
  val broker = db.broker
}