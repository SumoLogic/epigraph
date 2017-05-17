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

/* Created by yegor on 10/25/16. */

package ws.epigraph.compiler

import java.util.Collections

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class EpigraphCompilerTest extends FlatSpec with Matchers {
  behavior of "Epigraph Compiler"

  it should "detect incompatible overridden fields" in {
    val compiler = new EpigraphCompiler(
      Collections.singleton(
        new ResourceSource("/ws/epigraph/compiler/tests/incompatibleFields.epigraph")
      )
    )
    val errors = intercept[EpigraphCompilerException](compiler.compile()).errors
    errors.size() shouldBe 1
    errors.iterator().next().toString should include("is not a subtype")
  }

// FIXME uncomment once compiler gets fixed
//  it should "fail on tags of vartype types" in {
//    val compiler = new EpigraphCompiler(
//      Collections.singleton(
//        new ResourceSource("/ws/epigraph/compiler/tests/badTagType.epigraph")
//      )
//    )
//    val errors = intercept[EpigraphCompilerException](compiler.compile()).errors
//    errors.size() shouldBe 1
//    errors.iterator().next().toString should include("TODO")
//  }

  it should "collect types from resources" in {
    val compiler = new EpigraphCompiler(
      Collections.singleton(
        new ResourceSource("/ws/epigraph/compiler/tests/typesAndResources.epigraph")
      )
    )
    val cc = compiler.compile()
    cc.errors shouldBe empty
    cc.anonListTypes.size() shouldBe 2
    cc.resourcesSchemas.size() shouldBe 1

  }

}
