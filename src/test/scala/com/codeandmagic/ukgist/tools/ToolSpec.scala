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

package com.codeandmagic.ukgist.tools

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import java.lang.IllegalArgumentException
import java.io.{ByteArrayOutputStream, PrintStream}

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
class ToolSpec extends Specification{
  import ToolFixture._

  "TestTool" should{
    "print the help message if called with no arguments" in{
      val tool = spy(new TestTool()).apply()
      there was no(tool).execute()
      there was one(tool).help()
      tool.OUTPUT.toString must beEqualTo(tool.HELP_MESSAGE+"\n")
    }

    "call the execute() method if it has arguments" in{
      val tool = spy(new TestTool(FLAG1)).apply()
      there was one(tool).execute()
      //tool.OUTPUT.toString must beEqualTo(tool.EXECUTE_MESSAGE)
    }

    "correctly decode the string parameter of a flag" in{
      val tool = new TestTool(FLAG1,FLAG1_PARAM)
      val param = tool.getArgumentParameter(FLAG1,tool.defaultStringDeserializer,"","")
      param must beEqualTo(FLAG1_PARAM)
    }

    "correctly decode the custom parameter of a flag" in{
      val tool = new TestTool(FLAG1,FLAG2,FLAG3,FLAG3_PARAM)
      val param = tool.getArgumentParameter(FLAG3,INT_DESERIALIZER,FLAG3_PARAM,"")
      param must beEqualTo(FLAG3_PARAM_EXPECTED)
    }

    "throw an exception if the flag did not get a parameters" in{
      val tool = new TestTool(FLAG1,FLAG2)
      tool.getArgumentParameter(FLAG1,tool.defaultStringDeserializer,"","") must throwA(manifest[IllegalArgumentException])
      val tool2 = new TestTool(FLAG1, FLAG2)
      tool.getArgumentParameter(FLAG2,tool.defaultStringDeserializer,"","") must throwA(manifest[IllegalArgumentException])
    }

    "provide a default if the searched flag was not found" in{
      val tool = new TestTool()
      val param = tool.getArgumentParameter(FLAG1,tool.defaultStringDeserializer,FLAG2_PARAM,"")
      param must beEqualTo(FLAG2_PARAM)
      val param2 = tool.getArgumentParameter(FLAG3,INT_DESERIALIZER,FLAG3_DEFAULT,"")
      param2 must beEqualTo(FLAG3_DEFAULT)
    }
  }
}

object ToolFixture extends Mockito{
  class TestTool(override val args:String*) extends Tool(args:_*) {
    val OUTPUT = new ByteArrayOutputStream()
    val REQUIRED_PARAMETERS = 0
    override val OUT = new PrintStream(OUTPUT)
    override val HELP_MESSAGE = "abc"
    val EXECUTE_MESSAGE = "executed"

    override def apply():TestTool = {
      super.apply()
      this
    }

    def execute() {
      OUT.print(EXECUTE_MESSAGE)
    }
  }

  val FLAG1 = "--f1"
  val FLAG1_PARAM = "qwerty"
  val FLAG2 = "--f2"
  val FLAG2_PARAM = "ytrewq"
  val FLAG3 = "--f3"
  val FLAG3_PARAM = "123"
  val FLAG3_PARAM_EXPECTED = 123
  val FLAG3_DEFAULT = 42

  val INT_DESERIALIZER = (s:String)=>s.toInt
}
