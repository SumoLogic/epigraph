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
import ws.epigraph.java.JavaGenNames.{jn, ln, ttr}
import ws.epigraph.java.JavaGenUtils
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.{GenTagProjectionEntry, GenVarProjection}

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqVarProjectionGen extends ReqProjectionGen {
  type OpProjectionType <: GenVarProjection[_, OpTagProjectionEntryType, _]
  type OpTagProjectionEntryType <: GenTagProjectionEntry[OpTagProjectionEntryType, _]

  protected def op: OpProjectionType

  protected def tagGenerator(tpe: OpTagProjectionEntryType): ReqProjectionGen

  override lazy val children: Iterable[ReqProjectionGen] = tagGenerators.values

  // -----------

  protected val cType: CVarTypeDef = ReqProjectionGen.toCType(op.`type`()).asInstanceOf[CVarTypeDef]

  protected lazy val tagGenerators: Map[CTag, ReqProjectionGen] =
    op.tagProjections().values().map{ tpe =>
      (
        findTag(tpe.tag().name()),
        tagGenerator(tpe)
      )
    }.toMap

  protected def findTag(name: String): CTag = cType.effectiveTags.find(_.name == name).getOrElse{
    throw new RuntimeException(s"Can't find tag '$name' in type '${cType.name.toString}'")
  }

  // todo we have to deal with poly tails / normalization in generated classes

  protected def generate(
    reqVarProjectionFqn: Qn,
    reqTagProjectionEntryFqn: Qn
  ): String = {

    def genTag(tag: CTag, tagGenerator: ReqProjectionGen): CodeChunk = CodeChunk(
      /*@formatter:off*/sn"""\
  /**
   * @return ${tag.name} projection
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

    val tags = tagGenerators.map{ case (tag, gen) => genTag(tag, gen) }.foldLeft(CodeChunk.empty)(_ + _)

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      reqVarProjectionFqn.toString
    ) ++ tags.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ReqProjectionGen.generateImports(imports)}

/**
 * Request output projection for {@code ${ln(cType)}} type
 */
public class $shortClassName {
  private final @NotNull ${reqVarProjectionFqn.last()} raw;

  public $shortClassName(@NotNull ${reqVarProjectionFqn.last()} raw) { this.raw = raw; }
${tags.code}\

  public @NotNull ${reqVarProjectionFqn.last()} _raw() { return raw; }
}"""/*@formatter:on*/
  }
}
