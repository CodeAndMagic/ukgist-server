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

import org.orbroker.RowExtractor

/**
 * User: cvrabie
 * Date: 12/04/2013
 */
abstract class Entity(val id:Long) {
  def companion:Companion[_<:Entity]
  def copyWithId(newId: Long):Entity
}

trait Companion[+T]

trait Persistent[T]{
  this:Companion[T] =>
  def extractor:RowExtractor[T]
}
