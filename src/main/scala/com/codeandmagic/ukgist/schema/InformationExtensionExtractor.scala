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

package com.codeandmagic.ukgist.schema

import org.orbroker.{Row, RowExtractor}
import com.codeandmagic.ukgist.model.{PoliceCrimeData, Persistent, Discriminator, InformationExtension}

/**
 * User: cvrabie
 * Date: 30/04/2013
 */
object InformationExtensionExtractor extends RowExtractor[InformationExtension]{
  PoliceCrimeData.discriminator //TODO BAD!
  def extract(row: Row) = row.integer("infox_discriminator") match {
    case Some(discriminator) => Discriminator.findByDiscriminator(discriminator) match {
      //some voodoo magic
      case Some(instance:Persistent[_]) if instance.clazz <:< manifest[InformationExtension] =>
        instance.asInstanceOf[Persistent[_<:InformationExtension]].extractor.extract(row)
      case _ => throw new ClassCastException(("Discriminator %d is unknown or not for an InformationExtension. Is the discriminator " +
        "stable and the class holding it loaded? Has the class name changed?").format(row.integer("area_discriminator").get))
    }
    case _ => throw new ClassCastException("Row holds no information about discriminator!")
  }
}
