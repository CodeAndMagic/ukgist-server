package com.codeandmagic.ukgist.service

import com.vividsolutions.jts.index.strtree.STRtree
import com.codeandmagic.ukgist.util.GeometryUtils.{boundingBoxToEnvelope,locationToEnvelope}
import scala.collection.JavaConversions.asScalaBuffer
import com.codeandmagic.ukgist.model.{Location, Area}

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
class AreaIndex(val areas:Seq[Area]){
  private val tree = new STRtree

  //add all areas in the search index
  areas.foreach(a => tree.insert(a.boundingBox, a))

  /**
   * Returns all areas that contain this location within them
   * @param location
   * @return
   */
  def query(location:Location):Seq[Area] = tree
    //first fast-query the index to get the ones that have the location in their bounding box
    .query(location).map( _.asInstanceOf[Area] )
    //then select only the ones that exactly contain the location
    .filter( _.containsDefinitely(location) )

}
