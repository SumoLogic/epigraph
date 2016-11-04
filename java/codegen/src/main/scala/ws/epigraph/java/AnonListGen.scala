/* Created by yegor on 8/15/16. */

package ws.epigraph.java

import ws.epigraph.schema.compiler.{CAnonListType, CContext}

class AnonListGen(from: CAnonListType, ctx: CContext) extends ListGen[CAnonListType](from, ctx)
