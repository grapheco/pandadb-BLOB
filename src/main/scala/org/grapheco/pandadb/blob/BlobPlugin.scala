package org.grapheco.pandadb.blob

import org.grapheco.pandadb.plugin.typesystem.{ExtensionTypePlugin, FactoryManager, FunctionManager, TypeFactory}

class BlobPlugin extends ExtensionTypePlugin{

  override def getName: String = "BlobPlugin"

  override protected def registerAll(): Unit = {
    registerType(BlobType.instance, classOf[Blob]);
    registerFactory(BlobFactory);
    registerFunction(classOf[BlobFunctions])
  }

}
