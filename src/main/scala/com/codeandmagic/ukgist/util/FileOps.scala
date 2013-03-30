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
}

object FileOps{
  implicit def fileToFileOps(file:File) = new FileOps(file)
  implicit def fileOpsToFile(ops:FileOps) = ops.file
}


