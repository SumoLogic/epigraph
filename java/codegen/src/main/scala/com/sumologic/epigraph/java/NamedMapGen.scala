/* Created by yegor on 10/3/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.schema.compiler.{CContext, CMapTypeDef}

class NamedMapGen(from: CMapTypeDef, ctx: CContext) extends MapGen[CMapTypeDef](from, ctx) with DatumTypeJavaGen
