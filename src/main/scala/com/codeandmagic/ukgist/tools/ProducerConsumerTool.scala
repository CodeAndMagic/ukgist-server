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

import java.util.concurrent.LinkedBlockingQueue
import com.codeandmagic.ukgist.model.PoliceArea
import java.util
import net.liftweb.common.Logger

/**
 * User: cvrabie
 * Date: 13/05/2013
 */
abstract class ProducerConsumerTool[T](args:String*) extends Tool(args:_*) with Logger{
  val ONE:Boolean
  val cls:Class[T]
  def readOne():Unit
  def readMany():Unit
  def processBatch(items:Seq[T]):Seq[T]

  val QUEUE = new LinkedBlockingQueue[T]

  val PRODUCER_TASK:Runnable = new Runnable {
    def run() {
      info("Started producing %ss".format(cls.getSimpleName))
      if(ONE) readOne()
      else readMany()
      info("Stopped producing %ss".format(cls.getSimpleName))
    }
  }

  val PRODUCER = new Thread(PRODUCER_TASK)

  val SLEEP = 5000; //5s

  val CONSUMER_TASK:Runnable = new Runnable {
    def consume() {
      val batch = new util.ArrayList[T]()
      val count = QUEUE.drainTo(batch)
      debug("Queue offered us %d elements".format(batch.size))
      if(count > 0){
        val saved = processBatch( batch.toArray().asInstanceOf[Array[T]] )
        debug("Processed a batch of %d %ss".format(saved.size,cls.getSimpleName))
      }else{
        debug("Pausing for a while")
        try{ Thread.sleep(SLEEP) }catch {
          case _ => warn("Failed to sleep!")
        }
      }
    }

    def run() {
      info("Started consuming %ss".format(cls.getSimpleName))
      while(QUEUE.size() > 0 && PRODUCER.getState != Thread.State.TERMINATED){
        consume()
      }
      info("Stopped consuming %ss".format(cls.getSimpleName))
    }
  }

  val CONSUMER = new Thread(CONSUMER_TASK)

  def execute(){
    PRODUCER.start()
    CONSUMER.start()
  }

}
