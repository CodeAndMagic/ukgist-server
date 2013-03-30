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

package com.codeandmagic.ukgist.util

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import java.io.File

/**
 * User: cvrabie
 * Date: 30/03/2013
 */
class FileOpsSpec extends Specification with Mockito{

  "FileOps.extension" should{

    "return 'def' for a file 'abc.def'" in{
      f("abc.def").extension must beEqualTo("def")
    }

    "return 'ghi' for a file 'abc.def.ghi'" in{
      f("abc.def.ghi").extension must beEqualTo("ghi")
    }

    "return empty string for a file 'abc'" in{
      f("abc").extension must beEqualTo("")
    }

    "return empty string for a file 'abc.'" in{
      f("abc.").extension must beEqualTo("")
    }

    "return empty string for a file with no name" in{
      f("").extension must beEqualTo("")
    }

  }

  "FileOps.nameWithoutExtension" should{

    "return 'abc' for a file 'abc.def'" in{
      f("abc.def").nameWithoutExtension must beEqualTo("abc")
    }

    "return 'abc.def' for a file 'abc.def.ghi'" in{
      f("abc.def.ghi").nameWithoutExtension must beEqualTo("abc.def")
    }

    "return 'abc' for a file 'abc'" in{
      f("abc").nameWithoutExtension must beEqualTo("abc")
    }

    "return 'abc' for a file 'abc.'" in{
      f("abc.").nameWithoutExtension must beEqualTo("abc")
    }

    "return empty string for a file with no name" in{
      f("").nameWithoutExtension must beEqualTo("")
    }
  }


  def f(name:String) = new FileOps(new File(name))
}
