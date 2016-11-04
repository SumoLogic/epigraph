/* Created by yegor on 10/3/16. */

package ws.epigraph.java

import ws.epigraph.schema.compiler.{CContext, CMapTypeDef}

class NamedMapGen(from: CMapTypeDef, ctx: CContext) extends MapGen[CMapTypeDef](from, ctx) with DatumTypeJavaGen
