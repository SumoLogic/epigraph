/* Created by yegor on 10/25/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.Collections

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class CompilerSpec extends FlatSpec with Matchers {

  "SchemaCompiler" should "detect incompatible overridden fields" in {
    val compiler = new SchemaCompiler(
      Collections.singleton(
        new ResourceSource("/com/sumologic/epigraph/schema/compiler/tests/incompatibleFields.esc")
      )
    )
    val exception = intercept[SchemaCompilerException](compiler.compile())
    exception.errors shouldNot be('empty)
  }

}
