/*
 Copyright 2013 Cristian Vrabie, Evelina Vrabie

 This file is part of UKGist.

 UKGist is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 UKGist is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with UKGist.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.codeandmagic.ukgist.dao

import org.orbroker.Broker
import net.liftweb.util.Props
import java.io.{PrintWriter, File}
import org.orbroker.config.{FileSystemRegistrant, BrokerConfig, SimpleDataSource}
import com.codeandmagic.ukgist.util.{LogLevel, LogPrintWriter}
import com.codeandmagic.ukgist.schema.{InformationSchemaTokens, PoliceAreaSchemaTokens}
import net.liftweb.common.{Loggable, Failure, Full}
import scala.Exception
import scala.io.Source
import java.sql.Connection

/**
 * User: cvrabie
 * Date: 27/04/2013
 */
trait BrokerComponent {
  val broker:Broker
}
object ORBrokerFactory extends Loggable{
  def fromProps() = (for{
    dbDriver <- Props.get("db.driver") ?~! "Need the db.driver property"
    dbUrl <- Props.get("db.url") ?~! "Need the db.url property"
    dbUser <- Props.get("db.user") ?~! "Need the db.user property"
    dbPass <- Props.get("db.password") ?~! "Need the db.password property"
  }yield apply(dbDriver, dbUrl, dbUser, dbPass)) match {
    case Full(b) => b
    case f:Failure => throw new Exception(f.messageChain)
    case _ => throw new Exception("Could not initialize ORBroker")
  }

  def apply(dbDriver:String, dbUrl:String, dbUser:String, dbPass:String) = {
    val configFolder = new File(classOf[BrokerComponent].getResource("/sql").toURI)
    val dataSource = new SimpleDataSource(dbUrl, dbDriver)
    dataSource.setLogWriter(new PrintWriter(new LogPrintWriter(classOf[BrokerComponent],LogLevel.TRACE),true))
    val brokerConfig = new BrokerConfig(dataSource)
    brokerConfig.setUser(dbUser, dbPass)
    FileSystemRegistrant(configFolder).register(brokerConfig)
    brokerConfig.verify(PoliceAreaSchemaTokens.idSet)
    brokerConfig.verify(InformationSchemaTokens.idSet)
    Broker(brokerConfig)
  }

  private def loadSQL(relativePath:String) = {
    val file = Thread.currentThread().getContextClassLoader().getResource(relativePath).getPath
    val source = Source.fromFile(file)
    val builder = new StringBuilder
    source.getLines().foreach(builder.append(_))
    builder.toString()
  }

}