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

import com.codeandmagic.ukgist.model.{PoliceArea, Location, MonthInterval}
import net.liftweb.common.Loggable
import net.liftweb.http.rest.RestHelper

/**
 * User: cvrabie
 * Date: 09/03/2013
 */
object UKGistRest extends RestHelper with Loggable{
  serve {
    //crime/32.000,-0.54/2013-01
    case Get("crime" :: Location(loc) :: MonthInterval(mi) :: Nil, _) => <div><b>hello world!</b><p>{loc}</p>{mi}<p></p></div>

    //case JsonGet("areas" :: Nil, _) => PoliceArea.listAll()
  }
}
