package com.codeandmagic.ukgist.tools

import java.io.PrintStream

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
abstract class Tool(val args:String*){
  val OUT:PrintStream = System.out

  val argsWithIndex = args.zipWithIndex
  private val name = getClass.getSimpleName
  val PROGRAM_NAME = name.substring(0,name.indexOf("$") match {
    case i if i>0 => i
    case _ => name.length
  })

  def isArgumentParameter(i:Int) = i < args.length-1 && !args(i+1).startsWith("--")

  def getArgumentParameter[T](ARG_NAME:String,deserializer:(String)=>T,default:T, extraMsg:String):T =
    argsWithIndex.find(_ match {
      case (ARG_NAME,i) if isArgumentParameter(i) => true
      case (ARG_NAME,_) => throw new IllegalArgumentException("You need to specify a %s. %s".format(ARG_NAME,extraMsg))
      case _ => false
    }) match {
      case Some((_,i)) => deserializer(args(i+1))
      case None => default
    }

  val defaultStringDeserializer = (s:String) => s

  def apply():Tool = {
    if (args.size > 0) execute()
    else help()
    this
  }

  val HELP_MESSAGE:String
  def help() = OUT.println(HELP_MESSAGE)
  def execute()
}
