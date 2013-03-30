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
import org.joda.time.{Interval => JodaInterval, DateTime, YearMonth}
import org.joda.time.format.DateTimeFormat
import net.liftweb.util.Helpers.tryo

/**
 * User: cvrabie
 * Date: 23/03/2013
 */

/**
 * Wrapper on the JodaTime Interval class
 */
abstract class Interval {
  /**
   * Wrapped interval
   */
  val interval: JodaInterval
}

trait IntervalFactory[T <: Interval]{
  def unapply(str: String):Option[T]
}

class MonthInterval(dt:DateTime) extends Interval{
  private val firstDay = dt.withDayOfMonth(1).withTimeAtStartOfDay()
  private val lastDay = firstDay.plusMonths(1)
  val interval = new JodaInterval(firstDay, lastDay)
}

object MonthInterval extends IntervalFactory[MonthInterval]{
  val format = DateTimeFormat.forPattern("yyyy-MM").withZoneUTC()
  def unapply(str: String) = tryo{ format.parseDateTime(str) }.map(new MonthInterval(_)).toOption
}

class YearInterval(dt:DateTime) extends Interval{
  private val firstDay = dt.withDayOfYear(1).withTimeAtStartOfDay()
  private val lastDay = firstDay.plusYears(1)
  val interval = new JodaInterval(firstDay, lastDay)
}

object YearInterval extends IntervalFactory[YearInterval]{
  val format = DateTimeFormat.forPattern("yyyy").withZoneUTC()
  def unapply(str: String) = tryo{ format.parseDateTime(str) }.map(new YearInterval(_)).toOption
}