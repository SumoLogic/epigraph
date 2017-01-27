package ws.epigraph.java

import java.util.concurrent.ConcurrentHashMap

import ws.epigraph.compiler.CTypeName
import ws.epigraph.lang.Qn

/**
 * @author yegor 2016-12-15.
 */
class GenContext(val settings: GenSettings) {

  // type name -> Java type class FQN
  val generatedTypes: ConcurrentHashMap[CTypeName, Qn] = new ConcurrentHashMap

  val reqOutputProjections: ConcurrentHashMap[CTypeName, Qn] = new ConcurrentHashMap
}
