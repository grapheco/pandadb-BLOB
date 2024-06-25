package org.grapheco.pandadb.blob

import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpHead
import org.apache.http.impl.client.HttpClientBuilder
import org.grapheco.lynx.types.LynxType
import org.grapheco.pandadb.plugin.typesystem.TypeFactory

import java.io.{File, FileInputStream}
import java.net.URL

object BlobFactory extends TypeFactory[Blob]{
  private val httpClient = HttpClientBuilder.create().build();

  val EMPTY: Blob = fromBytes(Array.emptyByteArray)

  override def getType: LynxType = BlobType.instance

  override def fromBytes(bytes: Array[Byte]): Blob = new BytesBlob(bytes)

  override def fromString(string: String): Blob = fromURL(string)

  def fromFile(file: File): Blob = new StreamBlob(new FileInputStream(file), file.length)

  private def fromHttpURL(httpUrl: String): Blob = {
    val head = new HttpHead(httpUrl)
    val resp = httpClient.execute(head)
    val mime = resp.getFirstHeader("Content-Type").getValue
    val blob = new BytesBlob(IOUtils.toByteArray(resp.getEntity.getContent), _mimeType = Some(mime))
    resp.close()
    blob
  }

  def fromURL(url: String): Blob = {
//    TODO: Check URL.
    val p = "(?i)(http|https|file|ftp|ftps):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?".r
    val uri = p.findFirstIn(url).getOrElse(url)
    val lower = uri.toLowerCase()

    lower match {
      case l if l.startsWith("http://") => fromHttpURL(uri)
      case l if l.startsWith("https://") => fromHttpURL(uri)
      case l if l.startsWith("file://") => fromFile(new File(uri.substring(lower.indexOf("//") + 1)))
      case _ => fromBytes(IOUtils.toByteArray(new URL(uri)))
    }
  }


}
