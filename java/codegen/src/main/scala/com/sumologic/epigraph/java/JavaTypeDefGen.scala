/* Created by yegor on 7/12/16. */

package com.sumologic.epigraph.java

import java.nio.file.Path

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CContext, CType, CTypeDef}

abstract class JavaTypeDefGen[TypeDef >: Null <: CTypeDef](from: TypeDef, ctx: CContext)
    extends JavaTypeGen[TypeDef](from, ctx) {

  protected override def relativeFilePath: Path = { // TODO respect annotations changing namespace/type names for scala
    JavaGenUtils.fqnToPath(from.name.fqn.removeLastSegment()).resolve(from.name.local + ".java")
  }

}
