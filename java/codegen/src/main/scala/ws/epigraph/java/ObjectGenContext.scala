/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.java

import java.util.concurrent.atomic.AtomicInteger

import ws.epigraph.java.service.projections.req.CodeChunk
import ws.epigraph.lang.Qn

import scala.collection.mutable

/**
 * Object generation context. Contains elements to be added to the class: imports,
 * fields, methods.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ObjectGenContext(val gctx: GenContext, val namespace: Qn, val useQualifiedNames: Boolean = false) {
  private val _imports = new mutable.HashSet[String]()
  private val _fields = new mutable.HashSet[String]()
  private val _methods = new mutable.MutableList[CodeChunk]()
  private val _static = new mutable.MutableList[CodeChunk]()

  private val _visited = new mutable.HashSet[String]()

  private val methodUID: AtomicInteger = new AtomicInteger(0)

  // settings: take them from command line?

  val generateTextLocations = false

  //

  def use(i: String): String = {
    if (i == null || i.isEmpty) throw new IllegalArgumentException
    addImport(i, namespace) // todo make private
  }

  /**
   * Try to split `name` into namespace and class name by finding first segment starting with an upper case
   * add namespace if it doesn't equal `currentNs`
   *
   * @return class name to use
   */
  private def addImport(name: String, currentNs: Qn): String = if (useQualifiedNames) name else {
    val qn = Qn.fromDotSeparated(name)

    val csi = qn.segments.indexWhere(_.charAt(0).isUpper)

    val ns = if (csi < 0) qn else if (csi == qn.size()) Qn.EMPTY else qn.takeHeadSegments(csi)
    val shortClassName = if (csi < 0) null else if (csi == 0) qn else qn.removeHeadSegments(csi)
    val nameToImport = if (csi < 0 || csi == qn.size - 1) qn else qn.takeHeadSegments(csi + 1)

    if (!ns.isEmpty && ns != currentNs) _imports.add(nameToImport.toString)

    if (shortClassName == null) qn.toString else shortClassName.toString
  }

  def imports: List[String] = (_imports ++ _methods.flatMap(_.imports)).toList.sorted

  def addField(f: String): Boolean = {
    val res = !_fields.contains(f)
    if (res) _fields += f
    res
  }

  def fields: mutable.HashSet[String] = _fields

  def addMethod(m: String) { addMethod(CodeChunk(m)) }

  def addMethod(m: CodeChunk) { _methods += m }

  def methods: mutable.MutableList[CodeChunk] = _methods

  def addStatic(c: String) { addStatic(CodeChunk(c)) }

  def addStatic(c: CodeChunk) { _static += c }

  def static: mutable.MutableList[CodeChunk] = _static

  def nextMethodUID: Int = methodUID.getAndIncrement()

  def visited(name: String): Boolean = _visited.contains(name)

  def addVisited(name: String) { _visited.add(name) }
}
