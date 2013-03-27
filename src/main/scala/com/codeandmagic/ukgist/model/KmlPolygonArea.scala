package com.codeandmagic.ukgist.model

import de.micromata.opengis.kml.v_2_2_0.Kml
import com.codeandmagic.ukgist.util.KmlUtils

/**
 * User: cvrabie
 * Date: 27/03/2013
 */
class KmlPolygonArea(override val id:Long, override val name:String, override val kind:Area.Kind.Value, val kml:Kml)
  extends PolygonArea(id,name,kind,KmlUtils.kmlPolygonToJtsPolygon(kml))
