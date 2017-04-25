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

import ws.epigraph.compiler.{CTag, CVarTypeDef}
import ws.epigraph.java.JavaGenNames.{jn, ln, lqn2, ttr}
import ws.epigraph.java.JavaGenUtils
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.ReqTypeProjectionGen._
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.{GenTagProjectionEntry, GenVarProjection}

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqVarProjectionGen extends ReqTypeProjectionGen {
  override type OpProjectionType <: GenVarProjection[OpProjectionType, OpTagProjectionEntryType, _]
  type OpTagProjectionEntryType <: GenTagProjectionEntry[OpTagProjectionEntryType, _]

  protected def tagGenerator(tpe: OpTagProjectionEntryType): ReqProjectionGen

  protected def tailGenerator(op: OpProjectionType, normalized: Boolean): ReqProjectionGen

  // -----------

  protected def genShortClassName(prefix: String, suffix: String): String = genShortClassName(prefix, suffix, cType)

  override lazy val children: Iterable[ReqProjectionGen] =
    tagGenerators.values ++ tailGenerators.values ++ normalizedTailGenerators.values

  override protected val cType: CVarTypeDef = JavaGenUtils.toCType(op.`type`()).asInstanceOf[CVarTypeDef]

  protected lazy val tagGenerators: Map[CTag, ReqProjectionGen] =
    op.tagProjections().values().map { tpe => findTag(tpe.tag().name()) -> tagGenerator(tpe) }.toMap

  protected def findTag(name: String): CTag = cType.effectiveTags.find(_.name == name).getOrElse {
    throw new RuntimeException(s"Can't find tag '$name' in type '${ cType.name.toString }'")
  }

  protected lazy val tailGenerators: Map[OpProjectionType, ReqProjectionGen] =
    Option(op.polymorphicTails()).map(
      _.map { t: OpProjectionType => t -> tailGenerator(t, normalized = false) }.toMap
    ).getOrElse(Map())

  protected lazy val normalizedTailGenerators: Map[OpProjectionType, ReqProjectionGen] =
    Option(op.polymorphicTails()).map(
      _.map { t: OpProjectionType =>
        t -> tailGenerator(op.normalizedForType(t.`type`()), normalized = true)
      }.toMap
    ).getOrElse(Map())

  protected def generate(
    reqVarProjectionFqn: Qn,
    reqTagProjectionEntryFqn: Qn
  ): String = {

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
  public @Nullable ${tailGenerator.fullClassName} ${tailMethodPrefix(false)}${typeNameToMethodName(tailCtype)}${tailMethodSuffix(false)}() {
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
  public @NotNull ${tailGenerator.fullClassName} ${tailMethodPrefix(true)}${typeNameToMethodName(tailCtype)}${tailMethodSuffix(true)}() {
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
