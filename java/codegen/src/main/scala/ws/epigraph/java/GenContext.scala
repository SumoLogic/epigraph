package ws.epigraph.java

import java.util.concurrent.ConcurrentHashMap

import ws.epigraph.compiler.CTypeName

/**
 * @author yegor 2016-12-15.
 */
class GenContext(val settings: GenSettings) {

  val generatedTypes: ConcurrentHashMap[CTypeName, String] = new java.util.concurrent.ConcurrentHashMap

}
