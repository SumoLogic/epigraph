/* Created by yegor on 7/12/16. */

package ws.epigraph.java

import ws.epigraph.schema.compiler.{CContext, CListTypeDef}

class NamedListGen(from: CListTypeDef, ctx: CContext) extends ListGen[CListTypeDef](from, ctx) with DatumTypeJavaGen
