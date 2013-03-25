package com.codeandmagic.ukgist.model

import net.liftweb.util.Helpers._

/**
 * User: cvrabie
 * Date: 23/03/2013
 */
object Dbl{
  def unapply(str:String):Option[Double] = tryo{str.toDouble}.toOption
}
