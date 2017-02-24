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

import ws.epigraph.compiler.CDatumType
import ws.epigraph.java.JavaGenNames.{jn, lqn2}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.ReqModelProjectionGen._
import ws.epigraph.java.{JavaGen, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.AbstractOpModelProjection
import ws.epigraph.types.DatumTypeApi

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqModelProjectionGen extends ReqProjectionGen {
  type OpProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi]
  type OpMetaProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi]

  protected def op: OpProjectionType

  protected val cType: CDatumType = JavaGenUtils.toCType(op.model())

  protected def reqVarProjectionFqn: Qn

  protected def reqModelProjectionFqn: Qn

  protected def reqModelProjectionParams: String

  protected def metaGenerator(metaOp: OpMetaProjectionType): ReqProjectionGen =
    throw new RuntimeException("meta projections not supported")

  protected def tailGenerator(op: OpProjectionType, normalized: Boolean): ReqModelProjectionGen =
    throw new RuntimeException("tail projections not supported")

  // -----------

  override def children: Iterable[JavaGen] =
    super.children ++ metaGeneratorOpt.iterator ++ tailGenerators.values ++ normalizedTailGenerators.values

  protected lazy val params: CodeChunk =
    ReqProjectionGen.generateParams(op.params(), namespace.toString, "raw.params()")

  protected lazy val metaGeneratorOpt: Option[ReqProjectionGen] = {
    val metaOp: OpMetaProjectionType = op.metaProjection().asInstanceOf[OpMetaProjectionType]
    Option(metaOp).map(metaGenerator)
  }

  protected lazy val meta: CodeChunk = metaGeneratorOpt match {
    case Some(g) => CodeChunk(/*@formatter:off*/sn"""\
  public @Nullable ${g.fullClassName} meta() {
    return raw.metaProjection() == null ? null : new ${g.fullClassName} (raw.metaProjection());
  }
"""/*@formatter:on*/ , Set("org.jetbrains.annotations.Nullable")
    )
    case None => CodeChunk.empty
  }

  protected lazy val tails: CodeChunk = tailGenerators
    .map { case (tail, gen) => genTail(tail, gen) }
    .foldLeft(CodeChunk.empty)(_ + _)

  protected lazy val normalizedTails: CodeChunk = normalizedTailGenerators
    .map { case (tail, gen) => genNormalizedTail(tail, gen) }
    .foldLeft(CodeChunk.empty)(_ + _)

  protected lazy val tailGenerators: Map[OpProjectionType, ReqModelProjectionGen] =
    Option(op.polymorphicTails()).map(
      _.asInstanceOf[java.util.List[OpProjectionType]] // can't set SMP to OpProjectionType, Scala doesn't allow cyclic types
        .map { t: OpProjectionType => t -> tailGenerator(t, normalized = false) }.toMap
    ).getOrElse(Map())

  protected lazy val normalizedTailGenerators: Map[OpProjectionType, ReqModelProjectionGen] = {
    def ntg(
      op: OpProjectionType,
      visited: mutable.Set[CDatumType],
      includeSelf: Boolean): Map[OpProjectionType, ReqModelProjectionGen] = {

      val ct = JavaGenUtils.toCType(op.model())
      if (visited.contains(ct)) Map()
      else {
        visited.add(ct)
        var _res: Map[OpProjectionType, ReqModelProjectionGen] =
          if (includeSelf) Map(op -> tailGenerator(op, normalized = true)) else Map()

        Option(op.polymorphicTails()) match {
          case Some(_tails) =>

            for (tail <- _tails)
              _res ++= ntg(tail.asInstanceOf[OpProjectionType], visited, includeSelf = true)

          case None =>
        }

        _res
      }
    }

    ntg(op, mutable.Set(), includeSelf = false)
  }

  private def genTail(tail: OpProjectionType, tailGenerator: ReqModelProjectionGen): CodeChunk = {
    val tailCtype = JavaGenUtils.toCType(tail.model())
    CodeChunk(
      /*@formatter:off*/sn"""\
  /**
   * @return ${JavaGenUtils.javadocLink(tailCtype, namespace)} tail projection
   */
  public @Nullable ${tailGenerator.fullClassName} ${tailMethodPrefix(false)}${typeNameToMethodName(tailCtype, namespace.toString)}${tailMethodSuffix(false)}() {
    ${reqModelProjectionFqn.last()} tail = raw.tailByType(${lqn2(tailCtype, namespace.toString)}.Type.instance());
    return tail == null ? null : new ${tailGenerator.fullClassName}(tail);
  }
"""/*@formatter:on*/ ,
      Set(
        "org.jetbrains.annotations.Nullable"
      )
    )
  }

  def genNormalizedTail(tail: OpProjectionType, tailGenerator: ReqModelProjectionGen): CodeChunk = {
    val tailCtype = JavaGenUtils.toCType(tail.model())
    val tailTypeExpr = lqn2(tailCtype, namespace.toString)
    CodeChunk(
      /*@formatter:off*/sn"""\
  /**
   * @return model projection normalized for ${JavaGenUtils.javadocLink(tailCtype, namespace)} type
   *
   * @see <a href="https://github.com/SumoLogic/epigraph/wiki/polymorphic-tails#normalized-projections">normalized projections</a>
   */
  public @NotNull ${tailGenerator.fullClassName} ${tailMethodPrefix(true)}${typeNameToMethodName(tailCtype, namespace.toString)}${tailMethodSuffix(true)}() {
    return new ${tailGenerator.fullClassName}(raw.normalizedForType($tailTypeExpr.Type.instance()));
  }
"""/*@formatter:on*/ ,
      Set()
    )
  }

  protected def classJavadoc =/*@formatter:off*/sn"""\
/**
 * Request output projection for ${JavaGenUtils.javadocLink(cType, namespace)} type
 */
"""/*@formatter:on*/

}

object ReqModelProjectionGen {
  def tailPackageSuffix(normalized: Boolean): String = if (normalized) "_normalized" else "_tail"

  def tailMethodPrefix(normalized: Boolean): String = if (normalized) "normalizedFor_" else ""

  def tailMethodSuffix(normalized: Boolean): String = if (normalized) "" else "Tail"

  def typeNameToPackageName(cType: CDatumType, currentNamespace: String): String =
    jn(lqn2(cType, currentNamespace)).replace('.', '_').toLowerCase

  def typeNameToMethodName(cType: CDatumType, currentNamespace: String): String =
    JavaGenUtils.lo(jn(lqn2(cType, currentNamespace)).replace('.', '_'))
}
