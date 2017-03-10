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

import java.util.concurrent.ConcurrentHashMap

import ws.epigraph.compiler.CTypeName
import ws.epigraph.lang.Qn

/**
 * @author yegor 2016-12-15.
 */
class GenContext(val settings: GenSettings) {

  // type name -> Java type class FQN
  val generatedTypes: ConcurrentHashMap[CTypeName, Qn] = new ConcurrentHashMap

  val reqOutputProjections: java.util.Set[Qn] = ConcurrentHashMap.newKeySet()
  val reqInputProjections: java.util.Set[Qn] = ConcurrentHashMap.newKeySet()
  val reqUpdateProjections: java.util.Set[Qn] = ConcurrentHashMap.newKeySet()
  val reqDeleteProjections: java.util.Set[Qn] = ConcurrentHashMap.newKeySet()
  val reqPaths: java.util.Set[Qn] = ConcurrentHashMap.newKeySet()
}
