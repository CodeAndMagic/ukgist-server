package com.codeandmagic.ukgist.model

import org.specs2.mutable.Specification
import PolygonAreaFixture._

/**
 * User: cvrabie
 * Date: 25/03/2013
 */
class PolygonAreaSpec extends Specification{
  "Neighbourhood City of London 1" should{

    "contain %s inside the bounding box".format(LONDON_1_LOCATION_INSIDE_CONVEX_PART) in{
      LONDON_1_AREA.containsMaybe(LONDON_1_LOCATION_INSIDE_CONVEX_PART) must beTrue
    }

    "contain %s inside the bounding box".format(LONDON_1_LOCATION_OUTSIDE_CONCAVE_PART) in{
      LONDON_1_AREA.containsMaybe(LONDON_1_LOCATION_OUTSIDE_CONCAVE_PART) must beTrue
    }

    "NOT contain %s inside the bounding box".format(LONDON_1_LOCATION_OUTSIDE_BOX) in{
      LONDON_1_AREA.containsMaybe(LONDON_1_LOCATION_OUTSIDE_BOX) must beFalse
    }

    "contain %s inside the exact area".format(LONDON_1_LOCATION_INSIDE_CONVEX_PART) in{
      LONDON_1_AREA.containsDefinitely(LONDON_1_LOCATION_INSIDE_CONVEX_PART) must beTrue
    }

    "contain %s inside the exact area".format(LONDON_1_LOCATION_INSIDE_CONCAVE_PART) in{
      LONDON_1_AREA.containsDefinitely(LONDON_1_LOCATION_INSIDE_CONCAVE_PART) must beTrue
    }

    "NOT contain %s inside the exact area".format(LONDON_1_LOCATION_OUTSIDE_CONVEX_PART) in{
      LONDON_1_AREA.containsDefinitely(LONDON_1_LOCATION_OUTSIDE_CONVEX_PART) must beFalse
    }

    "NOT contain %s inside the exact area".format(LONDON_1_LOCATION_OUTSIDE_CONCAVE_PART) in{
      LONDON_1_AREA.containsDefinitely(LONDON_1_LOCATION_OUTSIDE_CONVEX_PART) must beFalse
    }

    "NOT contain %s inside the exact area".format(LONDON_1_LOCATION_OUTSIDE_BOX) in{
      LONDON_1_AREA.containsDefinitely(LONDON_1_LOCATION_OUTSIDE_BOX) must beFalse
    }
  }

}
