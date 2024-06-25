package org.grapheco.pandadb.blob

import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.procedure.ProcedureException
import org.grapheco.lynx.types.composite.LynxList
import org.grapheco.lynx.types.property.{LynxBoolean, LynxInteger, LynxString}
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions

import java.io.{File, FileInputStream}

class BlobFunctions extends TypeFunctions{

  @LynxProcedure(name="Blob.fromFile", description = "generate a blob object from the given file")
  def fromFile(@LynxProcedureArgument(name="filePath") path: LynxString): Blob = {
    val filePath = path.value
    if (filePath == null || filePath.trim.isEmpty) throw ProcedureException(s"invalid file path: $filePath");
    val file = new File(filePath);
    if (!file.exists()) throw ProcedureException(s"file not found: $filePath");
    BlobFactory.fromFile(file)
  }

  @LynxProcedure(name="Blob.fromURL", description = "load blob from an url")
  def fromURL(@LynxProcedureArgument(name="url") url: LynxString): Blob = BlobFactory.fromURL(url.value);

  @LynxProcedure(name="Blob.fromUTF8String", description = "generate a blob object from the given file")
  def fromUTF8String(@LynxProcedureArgument(name="text") lynxText: LynxString): Blob = {
    BlobFactory.fromBytes(lynxText.value.getBytes("utf-8"));
  }

  @LynxProcedure(name="Blob.fromString", description = "generate a blob object from the given file")
  def fromString(@LynxProcedureArgument(name="text") text: LynxString, @LynxProcedureArgument(name="encoding") encoding: LynxString): Blob = {
    BlobFactory.fromBytes(text.value.getBytes(encoding.value));
  }

  @LynxProcedure(name="Blob.fromBytes", description = "generate a blob object from the given file")
  def fromBytes(@LynxProcedureArgument(name="bytes") byteList: LynxList): Blob = {
    val bytes: Array[Byte] = byteList.value.asInstanceOf[List[Byte]].toArray
    BlobFactory.fromBytes(bytes);
  }

//  @LynxProcedure(name="Bytes.guessType", description = "guess mime type of a byte array")
//  def guessBytesMimeType(@LynxProcedureArgument(name="bytes") byteList: LynxList): String = {
//    byteList.value match {
//      case bytes: List[Byte] => MimeTypeFactory.guessMimeType(bytes.toArray).toString;
//    }
//    val bytes: Array[Byte] = byteList.value.asInstanceOf[List[Byte]].toArray
//
//  }

  @LynxProcedure(name="Blob.guessType", description = "guess mime type of a blob")
  def guessBlobMimeType(@LynxProcedureArgument(name="blob") blob: Blob): String =
    MimeTypeFactory.guessMimeType(blob.toBytes).toString

  @LynxProcedure(name="Blob.empty", description = "generate an empty blob")
  def empty(): Blob = BlobFactory.EMPTY

  @LynxProcedure(name="Blob.len", description = "get length of a blob object")
  def getBlobLength(@LynxProcedureArgument(name="blob") blob: Blob): LynxInteger = LynxInteger(blob.length)

  @LynxProcedure(name="Blob.toString", description = "cast to a string")
  def cast2String(@LynxProcedureArgument(name="blob") blob: Blob,
                  @LynxProcedureArgument(name="encoding") encoding: LynxString): LynxString
  = LynxString(new String(blob.toBytes, encoding.value))

  @LynxProcedure(name="Blob.toUTF8String", description = "cast to a string in utf-8 encoding")
  def cast2UTF8String(@LynxProcedureArgument(name="blob") blob: Blob): LynxString =
    LynxString(new String(blob.toBytes, "utf-8"))

  @LynxProcedure(name="Blob.toBytes", description = "cast to a byte array")
  def cast2Bytes(@LynxProcedureArgument(name="blob") blob: Blob): Array[Byte] = blob.toBytes

  @LynxProcedure(name="Blob.mime", description = "get mime type of a blob object")
  def getMimeType(@LynxProcedureArgument(name="blob") blob: Blob): String = blob.mimeType.text


  @LynxProcedure(name="Blob.mime1", description = "get mime type of a blob object")
  def getMajorMimeType(@LynxProcedureArgument(name="blob") blob: Blob): String = blob.mimeType.text.split("/")(0)

  @LynxProcedure(name="Blob.mime2", description = "get mime type of a blob object")
  def getMinorMimeType(@LynxProcedureArgument(name="blob") blob: Blob): String = blob.mimeType.text.split("/")(1)

  @LynxProcedure(name = "Blob.isA", description = "determine if the blob is kind of specified mime type")
  def is(@LynxProcedureArgument(name = "blob") blob: Blob,
         @LynxProcedureArgument(name = "mimeType") mimeType: LynxString): Boolean = {

    val a = mimeType.value.split("/")
    if (a.length == 1)
      blob.mimeType.major.equalsIgnoreCase(a(0))
    else
      blob.mimeType.major.equalsIgnoreCase(a(0)) && blob.mimeType.minor.equalsIgnoreCase(a(1))

  }
}