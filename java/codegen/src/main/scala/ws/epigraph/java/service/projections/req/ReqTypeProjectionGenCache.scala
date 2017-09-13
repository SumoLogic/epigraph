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

import ws.epigraph.java.TryLaterException
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
   * @param refOpt               projection reference `Option`
   * @param hasParentGen         `true` if resulting generator, if created by `default` provider, will have a parent generator
   * @param isNormalized         `true` if resulting generator will create a normalized version (normalized tail) of some projection
   * @param generatedProjections a map of references to already created generators
   * @param default              generator factory to use in case of cache miss
   * @tparam G generator type
   *
   * @return generator instance
   */
  def lookup[G <: AbstractReqTypeProjectionGen](
    refOpt: Option[ProjectionReferenceName],
    hasParentGen: Boolean,
    isNormalized: Boolean,
    generatedProjections: java.util.Map[ProjectionReferenceName, AbstractReqTypeProjectionGen],
    default: => G): G = {

    refOpt.map { ref =>
      val existingGen = generatedProjections.get(ref)
      if (existingGen != null)
        existingGen.asInstanceOf[G]
      else {
        if (!hasParentGen && isNormalized) {
          // can't create new generator before parent generator has run (and produced this instance as one of it's children)
          throw new TryLaterException(s"Can't create generator for '$ref' because it's parent wasn't invoked yet")
        }

        val newGen = default
        // cache.put(ref, newGen) // gets updated by ReqTypeProjectionGen.shouldRun todo cleanup
        newGen
      }
    }.getOrElse(default)

//    default

  }
}
