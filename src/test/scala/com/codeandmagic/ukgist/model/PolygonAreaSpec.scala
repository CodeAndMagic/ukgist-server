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
