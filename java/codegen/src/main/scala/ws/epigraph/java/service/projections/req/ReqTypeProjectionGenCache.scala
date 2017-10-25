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

import ws.epigraph.projections.gen.ProjectionReferenceName

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
object ReqTypeProjectionGenCache {

  /**
   * Looks up projection generator by reference. Ensures that normalized tail generators
   * won't run before original projection generators.
   *
   * Consider the following schema:
   * ```
   * outputProjection fooProjection: Foo = ( foo ) ~Bar $barProjection = ( bar )
   * ```
   *
   * It results in an implicitly created top-level `barProjection` which holds
   * `fooProjection.normalizedFor(Bar.type)`.
   *
   * `fooProjection` generator will produce `barProjection` generator as one of it's children,
   * but if there are some other usages of `barProjection` they will cause generator creations too,
   * and these generators won't produce valid contents as they won't have `fooProjection` generator
   * as their parent and so won't be initialized properly, most importantly their namespaces will be wrong.
   *
   * This method makes necessary checks and throws `TryLaterException` if such situation is detected. Top-level
   * code should collect all generators whose execution caused this exception and try them again later. In our example
   * `foo` generator and it's children should've run by then, which resolves the problem (with additional help
   * of `ReqProjectionShouldRunStrategy` which won't allow `bar` generator to run twice)
   *
   * UPDATE this logic is now handled by ReqTypeProjectionGen::generate
   *
   * @param refOpt               projection reference `Option`
   * @param generatedProjections a map of references to already created generators
   * @param default              generator factory to use in case of cache miss
   * @tparam G generator type
   *
   * @return generator instance
   */
  def lookup[G <: ReqTypeProjectionGen](
    refOpt: Option[ProjectionReferenceName],
    generatedProjections: java.util.Map[ProjectionReferenceName, ReqTypeProjectionGen],
    default: => G): G = {

    refOpt.map { ref =>
      val existingGen = generatedProjections.get(ref)
      if (existingGen != null)
        existingGen.asInstanceOf[G]
      else {
        val newGen = default
        // generatedProjections.put(ref, newGen) // gets updated by ReqProjectionShouldRunStrategy.checkAndMark todo cleanup
        newGen
      }
    }.getOrElse(default)

  }
}
