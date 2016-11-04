/* Created by yegor on 7/12/16. */

package ws.epigraph.java

import ws.epigraph.schema.compiler.{CContext, CTypeDef}

abstract class JavaTypeDefGen[TypeDef >: Null <: CTypeDef](from: TypeDef, ctx: CContext)
    extends JavaTypeGen[TypeDef](from, ctx)
