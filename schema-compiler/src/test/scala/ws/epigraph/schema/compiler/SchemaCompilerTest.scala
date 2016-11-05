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

package ws.epigraph.schema.compiler

import java.util.Collections

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class SchemaCompilerTest extends FlatSpec with Matchers {

  "SchemaCompiler" should "detect incompatible overridden fields" in {
    val compiler = new SchemaCompiler(
      Collections.singleton(
        new ResourceSource("/ws/epigraph/schema/compiler/tests/incompatibleFields.esc")
      )
    )
    intercept[SchemaCompilerException](compiler.compile()).errors shouldNot be('empty)
  }

}
