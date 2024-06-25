package org.grapheco.pandadb.blob

import org.apache.commons.io.IOUtils
import org.grapheco.lynx.types.structural.LynxPropertyKey
import org.grapheco.lynx.types.{LynxType, LynxValue}
import org.grapheco.lynx.types.traits.{HasProperty, LynxComputable}
import org.grapheco.pandadb.plugin.typesystem.AnyType

import java.io.InputStream

class BytesBlob(val bytes: Array[Byte], val _mimeType: Option[String] = None) extends Blob {

  override def length: Long = bytes.length

  override def mimeType: MimeType = _mimeType match {
    case Some(text) => MimeTypeFactory.fromText(text)
    case None => MimeTypeFactory.guessMimeType(bytes)
  }

  override def toBytes: Array[Byte] = bytes
}

class StreamBlob(val streamSource: InputStream,
                 val _length: Long,
                 val _mimeType: Option[String] = None) extends Blob {

  private def offerStream[T](consume: InputStream => T): T = {
    val t = consume(streamSource)
    streamSource.close()
    t
  }

  override def toBytes: Array[Byte] = offerStream(IOUtils.toByteArray)

  override def length: Long = _length

  override def mimeType: MimeType = _mimeType match {
    case Some(text) => MimeTypeFactory.fromText(text)
    case None => MimeTypeFactory.guessMimeType(streamSource)
  }

}

abstract class Blob extends AnyType with HasProperty {

  def length: Long
  def mimeType: MimeType
  def toBytes: Array[Byte]

  override def toString = s"blob(length=${length},mime-type=${mimeType.text})"

  override def equals(obj: Any): Boolean = obj match {
//    case blob: Blob => {
//      length == blob.length &&
//      mimeType.code == blob.mimeType.code &&
//      mimeType.text == blob.mimeType.text &&
//      this.toBytes().equals(blob.toBytes())
//    }
    case _ => false
  }

  override def serialize(): Array[Byte] = toBytes

  override def value: Any = toBytes

  override def lynxType: LynxType = BlobType.instance

  override def keys: Seq[LynxPropertyKey] = BlobOpts.allPropertyNames.map(LynxPropertyKey)

  override def property(propertyKey: LynxPropertyKey): Option[LynxValue] = BlobOpts.extractProperty(propertyKey.value)

}

