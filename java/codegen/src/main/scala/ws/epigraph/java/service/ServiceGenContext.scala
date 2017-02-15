/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.java.service

import java.util.concurrent.atomic.AtomicInteger

import ws.epigraph.java.GenContext
import ws.epigraph.lang.Qn

import scala.collection.mutable

/**
 * Service class generation context. Contains elements to be added to the class: imports,
 * fields, methods.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ServiceGenContext(val gctx: GenContext) {
  private val _imports = new mutable.HashSet[String]()
  private val _fields = new mutable.MutableList[String]()
  private val _methods = new mutable.MutableList[String]()

  private val methodUID: AtomicInteger = new AtomicInteger(0)

  // settings: take them from command line?

  val generateTextLocations = false

  val generateSeparateMethodsForVarProjections = false

  //

  def addImport(i: String) {
    if (i == null || i.isEmpty) throw new IllegalArgumentException
    _imports.add(i)
  }

  def addImport(qn: Qn) {addImport(qn.toString)}

  /**
   * Try to split `name` into namespace and class name by finding first segment starting with an upper case
   * add namespace if it doesn't equal `currentNs`
   *
   * @return short class name or `null` if class name not found
   */
  def addImport(name: String, currentNs: Qn): String = {
    val qn = Qn.fromDotSeparated(name)

    val csi = qn.segments.indexWhere(_.charAt(0).isUpper)

    val ns = if (csi < 0) qn else if (csi == qn.size() - 1) Qn.EMPTY else qn.takeHeadSegments(csi + 1)
    val shortClassName = if (csi < 0) null else if (csi == 0) qn else qn.removeHeadSegments(csi)

    if (!ns.isEmpty && ns != currentNs) addImport(ns)

    if (shortClassName == null) null else shortClassName.toString
  }

  def imports: List[String] = _imports.toList.sorted

  def addField(f: String) {_fields += f}

  def fields: mutable.MutableList[String] = _fields

  def addMethod(m: String) {_methods += m}

  def methods: mutable.MutableList[String] = _methods

  def nextMethodUID: Int = methodUID.getAndIncrement()
}
