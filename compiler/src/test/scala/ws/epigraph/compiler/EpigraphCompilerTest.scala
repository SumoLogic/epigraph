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

/* Created by yegor on 10/25/16. */

package ws.epigraph.compiler

import java.util.Collections
import java.util.function.Consumer

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class EpigraphCompilerTest extends FlatSpec with Matchers {
  behavior of "Epigraph Compiler"

  it should "detect incompatible overridden fields" in {
    val compiler = new EpigraphCompiler(
      Collections.singleton(new ResourceSource("/ws/epigraph/compiler/tests/incompatibleFields.epigraph"))
    )
    val errors = intercept[EpigraphCompilerException](compiler.compile()).messages
    errors.size() shouldBe 1
    errors.iterator().next().toString should include("is not a subtype")
  }

  it should "fail on invalid field names" in {
    val compiler = new EpigraphCompiler(
      Collections.singleton(new ResourceSource("/ws/epigraph/compiler/tests/badFieldNames.epigraph"))
    )
    val errors = intercept[EpigraphCompilerException](compiler.compile()).messages
    errors.size() shouldBe 4
    errors.forEach { new Consumer[CMessage] {
      override def accept(msg: CMessage): Unit = msg.toString should include("Invalid field name")
    }}
  }

  it should "fail on tags of vartype types" in {
    val compiler = new EpigraphCompiler(
      Collections.singleton(new ResourceSource("/ws/epigraph/compiler/tests/badTagType.epigraph"))
    )
    val errors = intercept[EpigraphCompilerException](compiler.compile()).messages
    errors.size() shouldBe 1
    errors.iterator().next().toString should include("is not a datum type")
  }

  it should "fail on invalid tag names" in {
    val compiler = new EpigraphCompiler(
      Collections.singleton(new ResourceSource("/ws/epigraph/compiler/tests/badTagNames.epigraph"))
    )
    val errors = intercept[EpigraphCompilerException](compiler.compile()).messages
    errors.size() shouldBe 4
    errors.forEach { new Consumer[CMessage] {
      override def accept(msg: CMessage): Unit = msg.toString should include("Invalid tag name")
    }}
  }

  it should "collect types from resources" in {
    val compiler = new EpigraphCompiler(
      Collections.singleton(new ResourceSource("/ws/epigraph/compiler/tests/typesAndResources.epigraph"))
    )
    val cc = compiler.compile()
    cc.errors shouldBe empty
    cc.anonListTypes.size() shouldBe 2
    cc.resourcesSchemas.size() shouldBe 1

  }

}
