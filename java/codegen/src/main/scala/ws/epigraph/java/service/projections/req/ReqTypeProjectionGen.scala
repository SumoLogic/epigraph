/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.java.service.projections.req

import ws.epigraph.compiler.CType
import ws.epigraph.java.JavaGenNames.{jn, ln, lqn}
import ws.epigraph.java.JavaGenUtils
import ws.epigraph.lang.Qn
import ws.epigraph.types.TypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqTypeProjectionGen extends ReqProjectionGen {

  protected def generatedProjections: java.util.Set[Qn]

  protected def cType: CType

  protected def name: Option[Qn]

  // -----------

  override def shouldRun: Boolean = name.forall(name => !generatedProjections.contains(name))

//  override def namespace: Qn = name.map(_.removeLastSegment()).getOrElse(super.namespace)

  protected def genShortClassName(prefix: String, suffix: String, cType: CType): String = {
    val middle = name.map(_.last()).map(JavaGenUtils.up).getOrElse(ln(cType))
    s"$prefix$middle$suffix"
  }

  protected def tailNamespaceSuffix(tailType: TypeApi, normalized: Boolean): Qn =
    namespaceSuffix
      .append(if (normalized) "_normalized" else "_tail")
      .append(typeNameToPackageName(tailType))

  def typeNameToPackageName(_type: TypeApi): String =
    jn(lqn(JavaGenUtils.toCType(_type), cType)).replace('.', '_').toLowerCase

  def typeNameToMethodName(_type: CType): String = JavaGenUtils.lo(jn(lqn(_type, cType)).replace('.', '_'))
}

object ReqTypeProjectionGen {
  def tailMethodPrefix(normalized: Boolean): String = if (normalized) "normalizedFor_" else ""

  def tailMethodSuffix(normalized: Boolean): String = if (normalized) "" else "Tail"
}
