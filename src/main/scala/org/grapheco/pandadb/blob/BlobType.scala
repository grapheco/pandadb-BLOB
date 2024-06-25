package org.grapheco.pandadb.blob

import org.grapheco.lynx.types.{LTAny, LynxType}

object BlobType {
  val instance: BlobType = new BlobType {
    override def parentType: LynxType = LTAny

    override def toString: String = "Blob"
  }
}

sealed abstract class BlobType extends LynxType