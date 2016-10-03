/* Created by yegor on 7/12/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.schema.compiler.{CContext, CTypeDef}

abstract class JavaTypeDefGen[TypeDef >: Null <: CTypeDef](from: TypeDef, ctx: CContext)
    extends JavaTypeGen[TypeDef](from, ctx)
