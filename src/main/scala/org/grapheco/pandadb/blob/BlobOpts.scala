package org.grapheco.pandadb.blob

import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.property.{LynxNull, LynxString}
// aipm
object BlobOpts {

  def allPropertyNames: Seq[String] = {
    Seq.empty
  }

  def extractProperty(propertyName: String): Option[LynxValue] = {
    Option(LynxString("valueOf"+propertyName))
  }
}
