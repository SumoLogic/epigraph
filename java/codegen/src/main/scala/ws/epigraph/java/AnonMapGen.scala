/* Created by yegor on 9/25/16. */

package ws.epigraph.java

import ws.epigraph.schema.compiler.{CAnonMapType, CContext}

class AnonMapGen(from: CAnonMapType, ctx: CContext) extends MapGen[CAnonMapType](from, ctx) with DatumTypeJavaGen
