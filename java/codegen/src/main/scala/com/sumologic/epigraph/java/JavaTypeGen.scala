/* Created by yegor on 8/15/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CContext, CDataType, CType, CTypeRef, CVarTypeDef}

import scala.collection.JavaConversions._

abstract class JavaTypeGen[Type >: Null <: CType](from: Type, ctx: CContext) extends JavaGen[Type](from, ctx) {

  protected val t: Type = from

  /** local java name for type [[t]] */
  def ln: String = ln(t)

  def withParents(trans: (String) => String): String = {
    t.getLinearizedParentsReversed.map(" " + lqn(_, t, trans) + ",").mkString
  }

  def withParents(suffix: String): String = withParents(_ + suffix)

  def parents(suffix: String): String = parents(t, _ + suffix)

  def parents(t: CType, trans: String => String): String =
    t.getLinearizedParentsReversed.map(lqn(_, t, trans)).mkString(", ")

  def up(name: String): String = Character.toUpperCase(name.charAt(0)) + name.substring(1)

//  def listSupplier: String = ctx.getAnonListOf(t).map { lt => sn"""\
//
//    @Override
//    protected @NotNull java.util.function.Supplier<io.epigraph.types.ListType> listTypeSupplier() {
//      return () -> ${lqn(lt, t)}.Type.instance();
//    }
//"""
//  }.getOrElse("")

  def dataTypeExpr(dt: CDataType, lt: CType): String =
    s"new io.epigraph.types.DataType(${dt.polymorphic}, ${lqrn(dt.typeRef, lt)}.Type.instance(), ${dt.effectiveDefaultTagName.map(dttr(dt, _, t)).getOrElse("null")})"

  protected def vt(t: CType, yes: => String, no: => String): String = t match {
    case _: CVarTypeDef => yes
    case _ => no
  }

  protected def vt(tr: CTypeRef, yes: => String, no: => String): String = tr.resolved match {
    case _: CVarTypeDef => yes
    case _ => no
  }

}
