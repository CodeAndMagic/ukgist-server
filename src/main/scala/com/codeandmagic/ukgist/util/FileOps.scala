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

import java.io.File

/**
 * User: cvrabie
 * Date: 28/03/2013
 */
class FileOps(val file:File) {
  def this(path:String) = this(new File(path))

  lazy val extension = (file.getName, file.getName.lastIndexOf('.')) match {
    case (name, i) if i>=0 && i<name.length-1 => name.substring(i+1)
    case _ => ""
  }

  lazy val nameWithoutExtension = (file.getName, file.getName.lastIndexOf('.')) match {
    case (name, i) if i>0 => name.substring(0,i)
    case (name, _) => name
    case _ => ""
  }
}

object FileOps{
  implicit def fileToFileOps(file:File) = new FileOps(file)
  implicit def fileOpsToFile(ops:FileOps) = ops.file
}


