/* Created by yegor on 8/15/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.schema.compiler.{CAnonListType, CContext}

class AnonListGen(from: CAnonListType, ctx: CContext) extends ListGen[CAnonListType](from, ctx)
