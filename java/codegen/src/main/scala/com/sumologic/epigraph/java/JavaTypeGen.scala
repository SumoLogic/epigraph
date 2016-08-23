/* Created by yegor on 8/15/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.{CContext, CType}

import scala.collection.JavaConversions._

abstract class JavaTypeGen[Type >: Null <: CType](from: Type, ctx: CContext) extends JavaGen[Type](from, ctx) {

  protected val t: Type = from

  /** local java name for type [[t]] */
  def ln: String = ln(t)

  def withParents(trans: (String) => String): String = {
    t.getLinearizedParentsReversed.map(" " + lqn(_, t, trans) + ",").mkString
  }

  def withParents(suffix: String): String = withParents(_ + suffix)

  def up(name: String): String = Character.toUpperCase(name.charAt(0)) + name.substring(1)

  def listSupplier: String = ctx.getAnonListOf(t).map { lt => sn"""\

    @Override
    protected @NotNull java.util.function.Supplier<io.epigraph.types.ListType> listTypeSupplier() {
      return () -> ${lqn(lt, t)}.type;
    }
"""
  }.getOrElse("")

}
