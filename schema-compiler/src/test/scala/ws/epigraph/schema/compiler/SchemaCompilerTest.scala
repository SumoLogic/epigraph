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
