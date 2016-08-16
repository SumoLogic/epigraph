/* Created by yegor on 7/12/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.schema.compiler.{CContext, CListTypeDef}

class ListGen(from: CListTypeDef, ctx: CContext) extends JavaTypeDefGen[CListTypeDef](from, ctx) {

  protected override def generate: String = s"""
/*
 * Standard header
 */

package ${javaFqn(t.name.fqn.removeLastSegment())}

import com.sumologic.epigraph.xp.data._
import com.sumologic.epigraph.xp.data.immutable._
import com.sumologic.epigraph.xp.data.mutable._
import com.sumologic.epigraph.xp.types._

trait ${baseName(t)} extends${withParents(t, baseName)} ListDatum[${baseName(t)}]

trait ${immName(t)} extends${withParents(t, immName)} ${baseName(t)} with ImmListDatum[${baseName(t)}]

trait ${mutName(t)} extends${withParents(t, mutName)} ${baseName(t)} with MutListDatum[${baseName(t)}]

trait ${bldName(t)} extends ${baseName(t)} with ListDatumBuilder[${baseName(t)}]

object ${objName(t)} extends ListType[${baseName(t)}](namespace \\ "${baseName(t)}", Seq(${parentNames(t, objName)})) { // TODO add anonymous list to parents?

  override def createMutable: ${mutName(t)} = new ${mutImplName(t)}

  private class ${mutImplName(t)} extends ${mutName(t)} {
    override def dataType: ${objName(t)}.type = ${objName(t)}
  }

  override def createBuilder: ${bldName(t)} = new ${bldImplName(t)}

  private class ${bldImplName(t)} extends ${bldName(t)} {
    override def dataType: ${objName(t)}.type = ${objName(t)}
  }

}
""".trim

}
