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

package com.codeandmagic.ukgist.service

import com.vividsolutions.jts.index.strtree.STRtree
import com.codeandmagic.ukgist.util.GeometryUtils.{boundingBoxToEnvelope,locationToEnvelope}
import scala.collection.JavaConversions.asScalaBuffer
import com.codeandmagic.ukgist.model.{Location, Area}
import com.codeandmagic.ukgist.dao.PoliceAreaDaoComponent

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
trait AreaIndex{
  def query(location:Location):Seq[Area]
}

trait AreaIndexComponent{
  val areaIndex:AreaIndex
}

trait STRtreeAreaIndexComponent extends AreaIndexComponent{
  this:AreaDaoComponent =>

  class STRtreeAreaIndex(val areas:Seq[Area]){
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
}
