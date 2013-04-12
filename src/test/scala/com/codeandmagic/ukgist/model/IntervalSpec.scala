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
import org.joda.time.{DateTimeZone, DateTime}

/**
 * User: cvrabie
 * Date: 09/04/2013
 */
class IntervalSpec extends Specification{
  def d(year:Int, month:Int, day:Int) = Some(new DateTime(year,month,day,0,0,0,DateTimeZone.UTC))

  "Interval('2012-10-24')" should{
    "have a start date of 2012-10-24T00:00" in{
      Interval.unapply("2012-10-24") must beSome.which(_.from == d(2012,10,24))
      Interval.unapply("2012-10-24") must beSome.which(_.from != d(2012,10,25))
    }
    "have an end date of 2012-11-25T00:00" in{
      Interval.unapply("2012-11-25") must beSome.which(_.from == d(2012,11,25))
    }
  }

  "Interval('2008-02-29')" should{ //bisect year
    "have a start date of 2008-02-01T00:00" in{
      Interval.unapply("2008-02-29") must beSome.which(_.from == d(2008,2,29))
    }
    "have an end date of 2008-03-01T00:00" in{
      Interval.unapply("2008-02-29") must beSome.which(_.to == d(2008,3,1))
    }
  }

  "Interval('2009-02-29')" should{   //non-bisect year
    "be None" in{
      Interval.unapply("2009-02-29") must beNone
    }
  }

  "Interval('2012-04')" should{
    "have a start date of 2012-04-01T00:00" in{
      Interval.unapply("2012-04") must beSome.which(_.from == d(2012,4,1))
    }
    "have an end date of 2012-05-01T00:00" in{
      Interval.unapply("2012-04") must beSome.which(_.to == d(2012,5,1))
    }
  }

  "Interval('2012')" should{
    "have a start date of 2012-01-01T00:00" in{
      Interval.unapply("2012") must beSome.which(_.from == d(2012,1,1))
    }
    "have an end date of 2013-01-01T00:00" in{
      Interval.unapply("2012") must beSome.which(_.to == d(2013,1,1))
    }
  }

  "Interval('2012/')" should{
    "have a start date of 2012-01-01T00:00" in{
      Interval.unapply("2012/") must beSome.which(_.from == d(2012,1,1))
    }
    "not have an end date" in{
      Interval.unapply("2012/") must beSome.which(_.to == None)
    }
  }

  "Interval('/2012')" should{
    "not have a start date" in{
      Interval.unapply("/2012") must beSome.which(_.from == None)
    }
    "have an end date of 2013-01-01T00:00" in{
      Interval.unapply("/2012") must beSome.which(_.to == d(2013,1,1))
    }
  }
}
