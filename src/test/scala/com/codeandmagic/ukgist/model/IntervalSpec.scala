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
      Interval.unapply("2012-11-24") must beSome.which(_.to == d(2012,11,25))
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

  "Interval('abc')" should{
    "be None" in{
      Interval.unapply("abc") must beNone
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

  "Interval('2012/a')" should{
    "be None" in{
      Interval.unapply("2012/a") must beNone
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

  "Interval('2012-09/')" should{
      "have a start date of 2012-09-01T00:00" in{
          Interval.unapply("2012-09/") must beSome.which(_.from == d(2012,9,1))
      }
      "not have and end date" in{
          Interval.unapply("2012-09/") must beSome.which(_.to == None)
      }
  }

  "Interval('/2012-09')" should{
      "have not have a start date" in{
          Interval.unapply("/2012-09") must beSome.which(_.from == None)
      }
      "have an end date of 2012-10-01T00:00" in{
          Interval.unapply("/2012-09") must beSome.which(_.to == d(2012,10,1))
      }
  }

  "Interval('2012-ab/')" should{
    "be None" in{
      Interval.unapply("2012-ab/") must beNone
    }
  }

  "Interval('2012*09/')" should{
    "be None" in{
      Interval.unapply("2012*08/") must beNone
    }
  }

  "Interval('2012-09/2014-10')" should{
      "have a start date of 2012-09-01T00:00" in{
          Interval.unapply("2012-09/2014-10") must beSome.which(_.from == d(2012,9,1))
      }
      "have an end date of 2014-10-T00:00" in{
          Interval.unapply("2012-09/2014-10") must beSome.which(_.to == d(2014,11,1))
      }
  }

  "Interval('2012-09/2014-12')" should{  //rolling to next year
    "have a start date of 2012-09-01T00:00" in{
      Interval.unapply("2012-09/2014-12") must beSome.which(_.from == d(2012,9,1))
    }
    "have an end date of 2015-01-T00:00" in{
      Interval.unapply("2012-09/2014-12") must beSome.which(_.to == d(2015,1,1))
    }
  }

  "Interval('2012-09/2014')" should{ //mixing month interval with year interval not allowed
    "be None" in{
      Interval.unapply("2012-09/2014") must beNone
    }
  }

  "Interval('2012-09-05/2014-05-24')" should{
      "have a start date of 2012-09-05T00:00" in{
          Interval.unapply("2012-09-05/2014-05-24") must beSome.which(_.from == d(2012,9,5))
      }
      "have an end date of 2014-05-25T00:00" in{
          Interval.unapply("2012-09-05/2014-05-24") must beSome.which(_.to == d(2014,05,25))
      }
  }

  "Interval('2012-09-05/2014-05')" should{ //mixing day interval with month interval not allowed
    "be None" in{
      Interval.unapply("2012-09-05/2014-05") must beNone
    }
  }

  "Interval('2000-01-01/2004-12-31')" should{ //roll to next year
    "have a start date of 2000-01-01T00:00" in{
      Interval.unapply("2000-01-01/2004-12-31") must beSome.which(_.from == d(2000,1,1))
    }
    "have an end date of 2005-01-01T00:00" in{
      Interval.unapply("2000-01-01/2004-12-31") must beSome.which(_.to == d(2005,1,1))
    }
  }

  "Interval('2000-1-2/2004-3-4')" should{ //no leading zeros in dates
    "have a start date of 2000-01-01T00:00" in{
      Interval.unapply("2000-1-2/2004-3-4") must beSome.which(_.from == d(2000,1,2))
    }
    "have an end date of 2004-03-04T00:00" in{
      Interval.unapply("2000-1-1/2004-3-4") must beSome.which(_.to == d(2004,3,5))
    }
  }

  "Interval('2012-09-05/2012-20-05')" should{ //no 20th month
    "be None" in{
      Interval.unapply("2012-09-05/2012-20-05") must beNone
    }
  }

  "Interval('2012-ab-05/2012-10-05')" should{ //no ab month
    "be None" in{
      Interval.unapply("2012-ab-05/2012-10-05") must beNone
    }
  }

  "Interval('2012-01-02-05/2012-10-05/')" should{ //no ab month
    "be None" in{
      Interval.unapply("2012-ab-05/2012-10-05") must beNone
    }
  }

  "Interval('2000-01-02/2004-03-04/')" should{ //no extra slashes
    "be None" in{
      Interval.unapply("2000-01-02/2004-03-04/") must beNone
    }
  }

  "Interval('/2000-01-02/2004-03-04')" should{ //no extra slashes
    "be None" in{
      Interval.unapply("/2000-01-02/2004-03-04") must beNone
    }
  }

  "Interval('2000-01-02-2004-03-04')" should{ //no weird separator
    "be None" in{
      Interval.unapply("2000-01-02-2004-03-04") must beNone
    }
  }
}
