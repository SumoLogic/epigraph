/* Created by yegor on 8/15/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.schema.compiler.{CAnonListType, CAnonMapType, CContext, CType, CTypeDef}

abstract class JavaTypeGen[Type >: Null <: CType](from: Type, ctx: CContext) extends JavaGen[Type](from, ctx) {

  protected val t: Type = from

}
