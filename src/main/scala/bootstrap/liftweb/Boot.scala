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

package bootstrap.liftweb

import net.liftweb.http._
import _root_.net.liftweb.http.provider._
import com.codeandmagic.ukgist.UKGistRest
import com.codeandmagic.ukgist.dao.{ORBrokerFactory, BrokerInformationDaoComponent, BrokerPoliceAreaDaoComponent, BrokerComponent}
import com.codeandmagic.ukgist.service.STRtreeAreaIndexComponent
import com.codeandmagic.ukgist.model.PoliceArea

/*
 * WHERE ALL THE MAGIC HAPPENS
 * @see CakePattern http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di/
 */
object WebComponentRegistry
  extends BrokerComponent
  with BrokerPoliceAreaDaoComponent
  with BrokerInformationDaoComponent
  with STRtreeAreaIndexComponent
{
  val broker = ORBrokerFactory.fromProps()
  val policeAreaDao = new BrokerPoliceAreaDao
  val informationDao = new BrokerInformationDao
  val areaIndex = new STRtreeAreaIndex(policeAreaDao.listAll())
}

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {


  def boot {
    //LiftRules.addToPackages("com.codeandmagic.ukgist")

    /* Things that completely bypass LiftFilter */
    /*LiftRules.liftRequest.prepend({
      case Req("download" :: _, _, _) => false
      case Req("images" :: _, _, _) => false
      case Req("favicon" :: Nil, "ico", _) => false
    })*/

    /* Registers the main dispatch table for REST calls (as stateless API) */
    LiftRules.statelessDispatch.append(UKGistRest)

    /* Defines encoding as UTF-8 */
    LiftRules.early.append((req: HTTPRequest) => req.setCharacterEncoding("utf-8"))

    /* Use HTML5 for rendering */
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    /* Transforms exceptions to proper JSON responses */
    //LiftRules.exceptionHandler.prepend({ case (mode, req, ex) => ConvertableToResponse.toJsonResponse(ex) })

    /* Headers that allow jQuery requests to work properly even if Origin header is null */
    /*LiftRules.supplimentalHeaders = s => s.addHeaders(
      List(HTTPParam("X-Lift-Version", LiftRules.liftVersion),
        HTTPParam("Access-Control-Allow-Origin", "*"),
        HTTPParam("Access-Control-Allow-Credentials", "true"),
        HTTPParam("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS"),
        HTTPParam("Access-Control-Allow-Headers", "Authorization,Keep-Alive,User-Agent,Cache-Control,Content-Type")
      ))
    */
  }
}