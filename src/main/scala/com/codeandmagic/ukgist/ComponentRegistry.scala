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

package com.codeandmagic.ukgist

import com.codeandmagic.ukgist.dao.{ORBrokerFactory, BrokerComponent, BrokerPoliceAreaDaoComponent}

/**
 * User: cvrabie
 * Date: 27/04/2013
 * WHERE ALL THE MAGIC HAPPENDS
 * @see CakePattern http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di/
 */
object ComponentRegistry extends BrokerComponent with BrokerPoliceAreaDaoComponent{
  val broker = ORBrokerFactory.fromProps()
  val policeAreaDao = new BrokerPoliceAreaDao
}
