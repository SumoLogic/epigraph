/* Created by yegor on 7/13/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.schema.compiler.{CContext, CTag, CTypeDef, CVarTypeDef}

class VarTypeGen(from: CVarTypeDef, ctx: CContext) extends JavaTypeDefGen[CVarTypeDef](from, ctx) {

  protected def generate: String = s"""
/*
 * Standard header
 */

package ${javaFqn(t.name.fqn.removeLastSegment())}

import com.sumologic.epigraph.xp.data._
import com.sumologic.epigraph.xp.data.immutable._
import com.sumologic.epigraph.xp.data.mutable._
import com.sumologic.epigraph.xp.types._

trait ${baseName(t)} extends${withParents(t, baseName)} Var[${baseName(t)}]

trait ${immName(t)} extends${withParents(t, immName)} ${baseName(t)} with ImmVar[${baseName(t)}]

trait ${mutName(t)} extends${withParents(t, mutName)} ${baseName(t)} with MutVar[${baseName(t)}]

trait ${bldName(t)} extends ${baseName(t)} with VarBuilder[${baseName(t)}]

object ${objName(t)} extends VarType[${baseName(t)}](namespace \\ "${baseName(t)}", Seq(${parentNames(t, objName)})) {
  ${
    t.effectiveTags.map { f => s"""
  val ${tagName(f)}: ${tagType(f, t)} = ${tagDef(f, t)}\n"""
    }.mkString
  }
  override def declaredFields: DeclaredFields = DeclaredFields(${t.declaredTags.map(tagName).mkString(", ")})

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

  private def tagName(f: CTag): String = jn(f.name)

  // TODO val _id: DatumField[FooId] = field("id", FooId)
  private def tagType(f: CTag, ht: CTypeDef): String = s"Tag[${ft(f, ht)}]"

  // TODO val _id: DatumField[FooId] = field("id", FooId)
  private def tagDef(f: CTag, ht: CTypeDef): String = s"""tag("${f.name}", ${ft(f, ht)})"""

  // TODO val _id: DatumField[FooId] = field("id", FooId)
  private def ft(f: CTag, ht: CTypeDef): String = s"${f.typeRef.resolved.name.name}"

}
