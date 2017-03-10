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

import ws.epigraph.compiler.{CTag, CType, CVarTypeDef}
import ws.epigraph.java.JavaGenNames.{jn, ln, lqn2, ttr}
import ws.epigraph.java.JavaGenUtils
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.ReqVarProjectionGen._
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.{GenTagProjectionEntry, GenVarProjection}

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqVarProjectionGen extends ReqProjectionGen {
  type OpProjectionType <: GenVarProjection[OpProjectionType, OpTagProjectionEntryType, _]
  type OpTagProjectionEntryType <: GenTagProjectionEntry[OpTagProjectionEntryType, _]

  protected def op: OpProjectionType

  protected def generatedProjections: java.util.Set[Qn]

  protected def tagGenerator(tpe: OpTagProjectionEntryType): ReqProjectionGen

  protected def tailGenerator(op: OpProjectionType, normalized: Boolean): ReqProjectionGen

  // -----------


  override def shouldRun: Boolean = Option(op.name()).forall(name => !generatedProjections.contains(name))

  override def namespace: Qn = Option(op.name()).map(_.removeLastSegment()).getOrElse(super.namespace)

  protected def genShortClassName(prefix: String, suffix: String): String = {
    val middle = Option(op.name()).map(_.last()).map(JavaGenUtils.up).getOrElse(ln(cType))
    s"$prefix$middle$suffix"
  }

  override lazy val children: Iterable[ReqProjectionGen] =
    tagGenerators.values ++ tailGenerators.values ++ normalizedTailGenerators.values

  protected val cType: CVarTypeDef = JavaGenUtils.toCType(op.`type`()).asInstanceOf[CVarTypeDef]

  protected lazy val tagGenerators: Map[CTag, ReqProjectionGen] =
    op.tagProjections().values().map { tpe => findTag(tpe.tag().name()) -> tagGenerator(tpe) }.toMap

  protected def findTag(name: String): CTag = cType.effectiveTags.find(_.name == name).getOrElse {
    throw new RuntimeException(s"Can't find tag '$name' in type '${ cType.name.toString }'")
  }

  protected lazy val tailGenerators: Map[OpProjectionType, ReqProjectionGen] =
    Option(op.polymorphicTails()).map(
      _.map { t: OpProjectionType => t -> tailGenerator(t, normalized = false) }.toMap
    ).getOrElse(Map())

  protected lazy val normalizedTailGenerators: Map[OpProjectionType, ReqProjectionGen] = {
    def ntg(
      op: OpProjectionType,
      visited: mutable.Set[CType],
      includeSelf: Boolean): Map[OpProjectionType, ReqProjectionGen] = {

      val ct = JavaGenUtils.toCType(op.`type`())
      if (visited.contains(ct)) Map()
      else {
        visited.add(ct)
        var _res: Map[OpProjectionType, ReqProjectionGen] =
          if (includeSelf) Map(op -> tailGenerator(op, normalized = true)) else Map()

        Option(op.polymorphicTails()) match {
          case Some(tails) =>

            for (tail <- tails)
              _res ++= ntg(tail, visited, includeSelf = true)

          case None =>
        }

        _res
      }
    }

    ntg(op, mutable.Set(), includeSelf = false)
  }

  protected def generate(
    reqVarProjectionFqn: Qn,
    reqTagProjectionEntryFqn: Qn
  ): String = {

    Option(op.name()).foreach(name => generatedProjections.add(name))

    def genTag(tag: CTag, tagGenerator: ReqProjectionGen): CodeChunk = CodeChunk(
      /*@formatter:off*/sn"""\
  /**
   * @return {@code ${tag.name}} projection
   */
  public @Nullable ${tagGenerator.shortClassName} ${jn(tag.name)}() {
    ${reqTagProjectionEntryFqn.last()} tpe = raw.tagProjections().get(${ttr(cType, tag.name, namespace.toString)}.name());
    return tpe == null ? null : new ${tagGenerator.shortClassName}(tpe.projection());
  }
"""/*@formatter:on*/ ,
      Set(
        tagGenerator.fullClassName,
        "org.jetbrains.annotations.Nullable",
        reqTagProjectionEntryFqn.toString
      )
    )

    def genTail(tail: OpProjectionType, tailGenerator: ReqProjectionGen): CodeChunk = {
      val tailCtype = JavaGenUtils.toCType(tail.`type`())
      CodeChunk(
        /*@formatter:off*/sn"""\
  /**
   * @return ${JavaGenUtils.javadocLink(tailCtype, namespace)} tail projection
   */
  public @Nullable ${tailGenerator.fullClassName} ${tailMethodPrefix(false)}${typeNameToMethodName(tailCtype, namespace.toString)}${tailMethodSuffix(false)}() {
    ${reqVarProjectionFqn.last()} tail = raw.tailByType(${lqn2(tailCtype, namespace.toString)}.Type.instance());
    return tail == null ? null : new ${tailGenerator.fullClassName}(tail);
  }
"""/*@formatter:on*/ ,
        Set(
          "org.jetbrains.annotations.Nullable"
        )
      )
    }

    def genNormalizedTail(tail: OpProjectionType, tailGenerator: ReqProjectionGen): CodeChunk = {
      val tailCtype = JavaGenUtils.toCType(tail.`type`())
      val tailTypeExpr = lqn2(tailCtype, namespace.toString)
      CodeChunk(
        /*@formatter:off*/sn"""\
  /**
   * @return var projection normalized for ${JavaGenUtils.javadocLink(tailCtype, namespace)} type
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

    val tags = tagGenerators.map { case (tag, gen) => genTag(tag, gen) }.foldLeft(CodeChunk.empty)(_ + _)
    val tails = tailGenerators.map { case (tail, gen) => genTail(tail, gen) }.foldLeft(CodeChunk.empty)(_ + _)
    val normalizedTails = normalizedTailGenerators
      .map { case (tail, gen) => genNormalizedTail(tail, gen) }
      .foldLeft(CodeChunk.empty)(_ + _)

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      reqVarProjectionFqn.toString
    ) ++ tags.imports ++ tails.imports ++ normalizedTails.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ReqProjectionGen.generateImports(imports)}

/**
 * Request output projection for {@code ${ln(cType)}} type
 */
public class $shortClassName {
  private final @NotNull ${reqVarProjectionFqn.last()} raw;

  public $shortClassName(@NotNull ${reqVarProjectionFqn.last()} raw) { this.raw = raw; }\
\s${(tags + tails + normalizedTails).code}\

  public @NotNull ${reqVarProjectionFqn.last()} _raw() { return raw; }
}"""/*@formatter:on*/
  }
}

object ReqVarProjectionGen {
  def tailPackageSuffix(normalized: Boolean): String = if (normalized) "_normalized" else "_tail"

  def tailMethodPrefix(normalized: Boolean): String = if (normalized) "normalizedFor_" else ""

  def tailMethodSuffix(normalized: Boolean): String = if (normalized) "" else "Tail"

  def typeNameToPackageName(cType: CType, currentNamespace: String): String =
    jn(lqn2(cType, currentNamespace)).replace('.', '_').toLowerCase

  def typeNameToMethodName(cType: CType, currentNamespace: String): String =
    JavaGenUtils.lo(jn(lqn2(cType, currentNamespace)).replace('.', '_'))
}
