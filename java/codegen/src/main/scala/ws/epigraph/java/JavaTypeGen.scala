/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 8/15/16. */

package ws.epigraph.java

import java.nio.file.Path

import ws.epigraph.schema.compiler.{CContext, CDataType, CType, CTypeRef, CVarTypeDef}

import scala.collection.JavaConversions._

abstract class JavaTypeGen[Type >: Null <: CType](from: Type, ctx: CContext) extends JavaGen[Type](from, ctx) {

  protected val t: Type = from

  // TODO respect annotations changing namespace/type names for scala
  protected override def relativeFilePath: Path =
  JavaGenUtils.fqnToPath(getNamedTypeComponent(t).name.fqn.removeLastSegment()).resolve(ln(t) + ".java")

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
//    protected @NotNull java.util.function.Supplier<ws.epigraph.types.ListType> listTypeSupplier() {
//      return () -> ${lqn(lt, t)}.Type.instance();
//    }
//"""
//  }.getOrElse("")

  def dataTypeExpr(dt: CDataType, lt: CType): String =
    s"new ws.epigraph.types.DataType(${lqrn(dt.typeRef, lt)}.Type.instance(), ${dt.effectiveDefaultTagName.map(dttr(dt, _, t)).getOrElse("null")})"

  protected def vt(t: CType, yes: => String, no: => String): String = t match {
    case _: CVarTypeDef => yes
    case _ => no
  }

  protected def vt(tr: CTypeRef, yes: => String, no: => String): String = tr.resolved match {
    case _: CVarTypeDef => yes
    case _ => no
  }

}
