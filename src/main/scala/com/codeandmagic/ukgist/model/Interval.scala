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