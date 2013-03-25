package com.codeandmagic.ukgist

import model.{Location, MonthInterval}
import net.liftweb.common.Loggable
import net.liftweb.http.rest.RestHelper

/**
 * User: cvrabie1
 * Date: 09/03/2013
 */
object UKGistRest extends RestHelper with Loggable{
  serve {
    //crime/32.000,-0.54/2013-01
    case Get("crime" :: Location(loc) :: MonthInterval(mi) :: Nil, _) => <div><b>hello world!</b><p>{loc}</p>{mi}<p></p></div>
  }
}
