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
import ws.epigraph.java.service.projections.req.AbstractReqTypeProjectionGen
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.ProjectionReferenceName

/**
 * @author yegor 2016-12-15.
 */
class GenContext(val settings: Settings) {

  // type name -> Java type class FQN
  val generatedTypes: ConcurrentHashMap[CTypeName, Qn] = new ConcurrentHashMap

  val reqOutputProjections: java.util.Map[ProjectionReferenceName, AbstractReqTypeProjectionGen] = new ConcurrentHashMap()
  val reqInputProjections: java.util.Map[ProjectionReferenceName, AbstractReqTypeProjectionGen] = new ConcurrentHashMap()
  val reqUpdateProjections: java.util.Map[ProjectionReferenceName, AbstractReqTypeProjectionGen] = new ConcurrentHashMap()
  val reqDeleteProjections: java.util.Map[ProjectionReferenceName, AbstractReqTypeProjectionGen] = new ConcurrentHashMap()
  val reqPaths: java.util.Map[ProjectionReferenceName, AbstractReqTypeProjectionGen] = new ConcurrentHashMap()

  /** Returns `true` if java 8 annotations are enabled. */
  def java8Annotations: Boolean = settings.java8Annotations()

}
