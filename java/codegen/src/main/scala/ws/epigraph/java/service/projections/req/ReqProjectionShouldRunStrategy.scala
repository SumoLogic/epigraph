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

package ws.epigraph.java.service.projections.req

import ws.epigraph.java.ShouldRunStrategy
import ws.epigraph.projections.gen.ProjectionReferenceName

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqProjectionShouldRunStrategy(
  gen: AbstractReqTypeProjectionGen,
  generatedProjections: java.util.Map[ProjectionReferenceName, AbstractReqTypeProjectionGen]
) extends ShouldRunStrategy {

  override def checkAndMark: Boolean = generatedProjections.synchronized {
    val shouldRun = gen.referenceNameOpt.forall(ref => !generatedProjections.containsKey(ref))
    if (shouldRun) {
      gen.referenceNameOpt.foreach(ref => generatedProjections.put(ref, gen))
    }
    shouldRun
  }

  // generatedProjections is a concurrent collection, no need for synchronized {}
  override def unmark(): Unit = gen.referenceNameOpt.foreach(ref => generatedProjections.remove(ref))
}
