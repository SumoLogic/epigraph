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

package ws.epigraph.java.service.assemblers

import java.nio.file.Path

import ws.epigraph.compiler._
import ws.epigraph.java.JavaGenNames.{jn, ln, lqn2}
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.projections.req.ReqProjectionGen
import ws.epigraph.java.service.projections.req.output.{ReqOutputProjectionGen, ReqOutputVarProjectionGen}
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class EntityAssemblerGen(g: ReqOutputVarProjectionGen, val ctx: GenContext) extends JavaGen {
  val cType: CType = JavaGenUtils.toCType(g.op.`type`())

  val shortClassName: String = ln(cType) + "Assembler"

  override def relativeFilePath: Path = JavaGenUtils.fqnToPath(g.namespace).resolve(shortClassName + ".java")

  private val hasTails = g.normalizedTailGenerators.nonEmpty

  override protected def generate: String = {
    val t = lqn2(cType, g.namespace.toString)

    case class TagParts(tag: CTag, tagGen: ReqOutputProjectionGen) {
      def tagName: String = jn(tag.name)

      def tagType: CType = tag.typeRef.resolved

      def tagAssemblerType: String = s"Assembler<? super D, ? super ${ tagGen.fullClassName }, ? extends ${
        lqn2(
          tagType,
          g.namespace.toString
        )
      }.Value>"

      def fbf: String = tag.name + "Assembler"

      def getter: String = tagName + "()"

      def setter: String = s"set${ JavaGenUtils.up(tag.name) }_"

      def dispatchTagInit: String = s"if (p.$getter != null) b.$setter($fbf.assemble(dto, p.$getter, ctx));"

      def javadoc: String = s"$fbf {@code $tagName} tag assembler"
    }

    def tagGenerators(g: ReqOutputVarProjectionGen): Map[String, (CTag, ReqProjectionGen)] =
      g.parentClassGenOpt.map(pg => tagGenerators(pg)).getOrElse(Map()) ++
      g.tagGenerators.map { case (tag, p) => tag.name -> (tag, p) }

    val fps: Seq[TagParts] = tagGenerators(g).map { case (_, (f, fg)) =>
      TagParts(f, fg.asInstanceOf[ReqOutputProjectionGen])
    }.toSeq

    case class TailParts(tailProjectionGen: ReqOutputVarProjectionGen) {
      def tt: CType = JavaGenUtils.toCType(tailProjectionGen.op.`type`())

      def tts: String = lqn2(tt, g.namespace.toString)

      def fbf: String = JavaGenUtils.lo(ln(tt)) + "Assembler"

      def fbft: String = s"Assembler<? super D, ? super ${ tailProjectionGen.fullClassName }, ? extends $tts>"

      def javadoc: String = s"$fbf {@code ${ ln(tt) }} value assembler"
    }

    val tps: Seq[TailParts] = g.normalizedTailGenerators.values.map { tg =>
      TailParts(tg.asInstanceOf[ReqOutputVarProjectionGen])
    }.toSeq

    val defaultBuild: String = /*@formatter:off*/sn"""\
AssemblerContext.Key key = new AssemblerContext.Key(dto, p);
Object visited = ctx.visited.get(key);
if (visited != null)
  return ($t) visited;
else {
  $t.Builder b = $t.create();
  ctx.visited.put(key, b);
${fps.map { fp => s"  if (p.${fp.getter} != null) b.${fp.setter}(${fp.fbf}.assemble(dto, p.${fp.getter}, ctx));" }.mkString("\n")}
  return b;
}
"""/*@formatter:on*/

    lazy val nonTailsBuild: String = /*@formatter:off*/sn"""{
      ${i(defaultBuild)}
    }"""/*@formatter:on*/

    lazy val tailsBuild: String = /*@formatter:off*/sn"""
      return ${g.shortClassName}.dispatcher.dispatch(
          p,
          ${if (hasTails) "typeExtractor.apply(dto)" else s"$t.type"},
${if (tps.nonEmpty) tps.map { tp => s"tp -> ${tp.fbf}.assemble(dto, tp, ctx)" }.mkString("          ",",\n          ",",\n") else ""}\
          () -> {
            ${i(defaultBuild)}
          }
      );"""/*@formatter:on*/

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "org.jetbrains.annotations.Nullable",
      "java.util.function.Function",
      "ws.epigraph.assembly.Assembler",
      "ws.epigraph.assembly.AssemblerContext",
      "ws.epigraph.types.Type"
    )

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package ${g.namespace};

${JavaGenUtils.generateImports(imports)}

/**
 * Assembler for {@code ${ln(cType)}} instance from data transfer object, driven by request output projection
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName<D> implements Assembler<@Nullable D, @NotNull ${g.shortClassName}, /*@NotNull*/ $t> {
${if (hasTails) "  private final @NotNull Function<? super D, Type> typeExtractor;\n" else "" }\
  //tag assemblers
${fps.map { fp => s"  private final @NotNull ${fp.tagAssemblerType} ${fp.fbf};"}.mkString("\n") }\
${if (hasTails) tps.map { tp => s"  private final @NotNull ${tp.fbft} ${tp.fbf};"}.mkString("\n  //tail assemblers\n","\n","") else "" }

  /**
   * Assembler constructor
   *
${if (hasTails) s"   * @param typeExtractor data type extractor, used to determine DTO type\n" else ""}\
${fps.map { fp => s"   * @param ${fp.javadoc}"}.mkString("\n") }\
${if (hasTails) tps.map { tp => s"   * @param ${tp.javadoc}"}.mkString("\n","\n","") else "" }
   */
  public $shortClassName(
${if (hasTails) s"    @NotNull Function<? super D, Type> typeExtractor,\n" else "" }\
${fps.map { fp => s"    @NotNull ${fp.tagAssemblerType} ${fp.fbf}"}.mkString(",\n") }\
${if (hasTails) tps.map { tp => s"    @NotNull ${tp.fbft} ${tp.fbf}"}.mkString(",\n", ",\n", "") else ""}
  ) {
${if (hasTails) s"    this.typeExtractor = typeExtractor;\n" else "" }\
${fps.map { fp => s"    this.${fp.fbf} = ${fp.fbf};"}.mkString("\n") }\
${if (hasTails) tps.map { tp => s"    this.${tp.fbf} = ${tp.fbf};"}.mkString("\n","\n","") else ""}
  }

  /**
   * Assembles {@code $t} instance from DTO
   *
   * @param dto data transfer object
   * @param p   request projection
   * @param ctx assembly context
   *
   * @return {@code $t} object
   */
  @Override
  public @NotNull $t assemble(@NotNull D dto, @NotNull ${g.shortClassName} p, @NotNull AssemblerContext ctx) {
    if (dto == null) {
      $t.Builder b = $t.create();
${fps.map { fp => s"      if (p.${fp.getter} != null) b.${fp.setter}Error(ws.epigraph.errors.ErrorValue.NULL);" }.mkString("", "\n", "\n")}\
      return b;
    } else ${if (hasTails) tailsBuild else nonTailsBuild}
  }
}"""/*@formatter:on*/
  }
}
