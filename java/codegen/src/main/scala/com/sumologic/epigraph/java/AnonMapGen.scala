/* Created by yegor on 9/25/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.schema.compiler.{CAnonMapType, CContext}

class AnonMapGen(from: CAnonMapType, ctx: CContext) extends MapGen[CAnonMapType](from, ctx) with DatumTypeJavaGen
