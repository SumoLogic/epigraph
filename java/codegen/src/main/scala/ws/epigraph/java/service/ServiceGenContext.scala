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

  val isDebug = false

  val generateSeparateMethodsForVarProjections = false

  //

  def addImport(i: String) {_imports.add(i)}

  def imports: List[String] = _imports.toList.sorted

  def addField(f: String) {_fields += f}

  def fields: mutable.MutableList[String] = _fields

  def addMethod(m: String) {_methods += m}

  def methods: mutable.MutableList[String] = _methods

  def nextMethodUID: Int = methodUID.getAndIncrement()
}
