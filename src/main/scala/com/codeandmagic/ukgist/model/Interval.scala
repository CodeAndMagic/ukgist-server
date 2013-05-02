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
import org.joda.time.{Interval => JodaInterval, DateTime}
import org.joda.time.format.DateTimeFormat
import net.liftweb.util.Helpers.tryo
import net.liftweb.json.JsonAST.{JNull, JInt, JField, JObject}

/**
 * User: cvrabie
 * Date: 23/03/2013
 */

/**
 * Wrapper on the JodaTime Interval class
 */
class Interval(val from:Option[DateTime], val to:Option[DateTime]){
  if(from.isDefined && to.isDefined && from.get.isAfter(to.get))
    throw new IllegalArgumentException("FROM can't be after TO!")
  /**
   * Wrapped interval
   */
  //val interval: JodaInterval = new JodaInterval(from.getOrElse(Interval.MIN), to.getOrElse(Interval.MAX))
  def this(i1:Interval, i2:Interval) = this(i1.from, i2.to)
  def this(from:Option[DateTime], i:Interval) = this(from, i.to)
  def this(from:DateTime, to:DateTime) = this(Some(from),Some(to))
  def this(i:Interval, to:Option[DateTime]) = this(i.from, to)

  private lazy val asString = from + "/" + to

  override def equals(p1: Any) = p1 match {
    case i:Interval => (from,to,i.from,i.to) match {  //important because DateTime equals does not work as expected
      case (Some(f1),Some(t1),Some(f2),Some(t2)) if f1.isEqual(f2) && t1.isEqual(t2) => true
      case (Some(f1),None,Some(f2),None) if f1.isEqual(f2) => true
      case (None, Some(t1), None, Some(t2)) if t1.isEqual(t2) => true
      case (None,None,None,None) => true
      case _ => false
    }
    case _ => false
  }

  def isEnclosing(that: Interval) = (this.from, that.from, this.to, that.to) match {
    case (None,_,None,_) => true
    case (Some(_),None,_,_) => false
    case (_,_,Some(_),None) => false
    case (Some(f1),Some(f2),Some(t1),Some(t2))
      if (f1.isEqual(f2)||f1.isBefore(f2)) && (t1.isEqual(t2)||t1.isAfter(t2)) => true
    case _ => false
  }

  def isEnclosed(that: Interval) = that.isEnclosing(this)

  override def toString = asString

  protected lazy val json = JObject(List(
    JField("from", from.map(f=>JInt(f.getMillis)).getOrElse(JNull)),
    JField("to", to.map(t=>JInt(t.getMillis)).getOrElse(JNull))
  ))

  def toJson = json
}

trait IntervalFactory[T <: Interval]{
  def unapply(str: String):Option[T]
}

object Interval extends IntervalFactory[Interval]{
  //val MIN = new DateTime(0L)
  //val MAX = new DateTime(4294967295L)

  def unapply(str: String):Option[Interval] = str.trim.split("/",2).toList match {
    case "" :: Nil => Some(FOREVER)

    case DayInterval(d) :: Nil => Some(d)
    case "" :: DayInterval(d) :: Nil => Some(new Interval(None, d))
    case DayInterval(d) :: "" :: Nil => Some(new Interval(d, None))
    case DayInterval(d1) :: DayInterval(d2) :: Nil => Some(new Interval(d1, d2))

    case MonthInterval(m) :: Nil => Some(m)
    case ""  :: MonthInterval(m) :: Nil => Some(new Interval(None, m))
    case MonthInterval(m) :: ""  :: Nil => Some(new Interval(m, None))
    case MonthInterval(m1) :: MonthInterval(m2) :: Nil => Some(new Interval(m1, m2))

    case YearInterval(y) :: Nil => Some(y)
    case "" :: YearInterval(y) :: Nil => Some(new Interval(None, y))
    case YearInterval(y) :: "" :: Nil => Some(new Interval(y, None))
    case YearInterval(y1) :: YearInterval(y2) :: Nil => Some(new Interval(y1, y2))
    case _ => None
  }

  def unapply(i:Interval):Option[(Option[DateTime],Option[DateTime])] = Some((i.from, i.to))

  object FOREVER extends Interval(None,None)
}

class DayInterval(dt:DateTime) extends Interval(
  Some(dt.withTimeAtStartOfDay()),
  Some(dt.withTimeAtStartOfDay().plusDays(1))
){
  def this(year:Int, month:Int, day:Int) = this(new DateTime(year,month,day,0,0))
}

object DayInterval extends IntervalFactory[DayInterval]{
  val format = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC()
  def unapply(str: String) = tryo{ format.parseDateTime(str) }.map(new DayInterval(_)).toOption
}

class MonthInterval(dt:DateTime) extends Interval(
  Some(dt.withDayOfMonth(1).withTimeAtStartOfDay()),
  Some(dt.withDayOfMonth(1).withTimeAtStartOfDay().plusMonths(1))
){
  def this(year:Int, month:Int) = this(new DateTime(year,month,1,0,0))
}

object MonthInterval extends IntervalFactory[MonthInterval]{
  val format = DateTimeFormat.forPattern("yyyy-MM").withZoneUTC()
  def unapply(str: String) = tryo{ format.parseDateTime(str) }.map(new MonthInterval(_)).toOption
}

class YearInterval(dt:DateTime) extends Interval(
  Some(dt.withDayOfYear(1).withTimeAtStartOfDay()),
  Some(dt.withDayOfYear(1).withTimeAtStartOfDay().plusYears(1))
){
  def this(year:Int) = this(new DateTime(year,1,1,0,0))
}

object YearInterval extends IntervalFactory[YearInterval]{
  val format = DateTimeFormat.forPattern("yyyy").withZoneUTC()
  def unapply(str: String) = tryo{ format.parseDateTime(str) }.map(new YearInterval(_)).toOption
}