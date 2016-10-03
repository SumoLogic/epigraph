/* Created by yegor on 7/12/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.schema.compiler.{CContext, CListTypeDef}

class NamedListGen(from: CListTypeDef, ctx: CContext) extends ListGen[CListTypeDef](from, ctx) with DatumTypeJavaGen
