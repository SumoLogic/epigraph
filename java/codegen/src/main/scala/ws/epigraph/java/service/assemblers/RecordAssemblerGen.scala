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

import ws.epigraph.compiler.{CDatumType, CField, CType, CTypeKind}
import ws.epigraph.java.JavaGenNames.{jn, ln, lqn2}
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.projections.req.output.{ReqOutputModelProjectionGen, ReqOutputProjectionGen, ReqOutputRecordModelProjectionGen}
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class RecordAssemblerGen(g: ReqOutputRecordModelProjectionGen, val ctx: GenContext) extends JavaGen {
  val cType: CDatumType = JavaGenUtils.toCType(g.op.`type`())

  val shortClassName: String = ln(cType) + "Assembler"

  override protected def relativeFilePath: Path = JavaGenUtils.fqnToPath(g.namespace).resolve(shortClassName + ".java")

  private val hasTails = g.normalizedTailGenerators.nonEmpty

  override protected def generate: String = {
    val t = lqn2(cType, g.namespace.toString)

    case class FieldParts(field: CField, fieldGen: ReqOutputProjectionGen) {
      def fieldName: String = jn(field.name)

      def fieldType: CType = field.typeRef.resolved

      def isEntity: Boolean = fieldType.kind == CTypeKind.ENTITY

      def fieldBuilderType: String = s"BiFunction<? super D, ? super ${ fieldGen.fullClassName }, ? extends ${lqn2(fieldType, g.namespace.toString)}${if (isEntity) "" else ".Value"}>"

      def fbf: String = field.name + "Builder"

      def getter: String = fieldName + "()"

      def setter: String = "set" + JavaGenUtils.up(field.name) + (if (isEntity) "" else "_")

      def dispatchFieldInit: String = s"if (p.$getter != null) b.$setter($fbf.apply(dto, p.$getter));"

      def javadoc: String = s"$fbf {@code $fieldName} field builder"
    }

    val fps: Seq[FieldParts] = g.fieldGenerators.map { case (f, fg) =>
      FieldParts( f, fg.dataProjectionGen )
    }.toSeq

    case class TailParts(tailProjectionGen: ReqOutputModelProjectionGen) {
      def tt: CDatumType = JavaGenUtils.toCType(tailProjectionGen.op.`type`())

      def tts: String = lqn2(tt, g.namespace.toString)

      def fbf: String = JavaGenUtils.lo(ln(tt)) + "Builder"

      def fbft: String = s"BiFunction<? super D, ? super ${ tailProjectionGen.fullClassName }, ? extends $tts.Value>"

      def javadoc: String = s"$fbf {@code ${ln(tt)}} value builder"
    }

    val tps: Seq[TailParts] = g.normalizedTailGenerators.values.map { tg =>
      TailParts(tg.asInstanceOf[ReqOutputModelProjectionGen])
    }.toSeq

    val defaultBuild: String = /*@formatter:off*/sn"""\
$t.Builder b = $t.create();
${fps.map { fp => s"if (p.${fp.getter} != null) b.${fp.setter}(${fp.fbf}.apply(dto, p.${fp.getter}));" }.mkString("\n")}
return b.asValue();
"""/*@formatter:on*/

    lazy val nonTailsBuild: String = /*@formatter:off*/sn"""{
      ${i(defaultBuild)}
    }"""/*@formatter:on*/

    lazy val tailsBuild: String = /*@formatter:off*/sn"""
      return ${g.shortClassName}.dispatcher.dispatch(
          p,
          ${if (hasTails) "typeExtractor.apply(dto)" else s"$t.type"},
${if (tps.nonEmpty) tps.map { tp => s"tp -> ${tp.fbf}.apply(dto, tp)" }.mkString("          ",",\n          ",",\n") else ""}\
          () -> {
            ${i(defaultBuild)}
          }
      );"""/*@formatter:on*/

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "org.jetbrains.annotations.Nullable",
      "java.util.function.BiFunction",
      "java.util.function.Function",
      "ws.epigraph.types.Type"
    )

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package ${g.namespace};

${JavaGenUtils.generateImports(imports)}

/**
 * Value assembler for {@code ${ln(cType)}} type, driven by request output projection
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName<D> implements BiFunction<@Nullable D, @NotNull ${g.shortClassName}, /*@NotNull*/ $t.Value> {
${if (hasTails) "  private final @NotNull Function<? super D, Type> typeExtractor;\n" else "" }\
  //field builders
${fps.map { fp => s"  private final @NotNull ${fp.fieldBuilderType} ${fp.fbf};"}.mkString("\n") }\
${if (hasTails) tps.map { tp => s"  private final @NotNull ${tp.fbft} ${tp.fbf};"}.mkString("\n  //tail builders\n","\n","") else "" }

  /**
   * Assembler constructor
   *
${if (hasTails) s"   * @param typeExtractor data type extractor, used to determine DTO type\n" else ""}\
${fps.map { fp => s"   * @param ${fp.javadoc}"}.mkString("\n") }\
${if (hasTails) tps.map { tp => s"   * @param ${tp.javadoc}"}.mkString("\n","\n","") else "" }
   */
  public $shortClassName(
${if (hasTails) s"    @NotNull Function<? super D, Type> typeExtractor,\n" else "" }\
${fps.map { fp => s"    @NotNull ${fp.fieldBuilderType} ${fp.fbf}"}.mkString(",\n") }\
${if (hasTails) tps.map { tp => s"    @NotNull ${tp.fbft} ${tp.fbf}"}.mkString(",\n", ",\n", "") else ""}
  ) {
${if (hasTails) s"    this.typeExtractor = typeExtractor;\n" else "" }\
${fps.map { fp => s"    this.${fp.fbf} = ${fp.fbf};"}.mkString("\n") }\
${if (hasTails) tps.map { tp => s"    this.${tp.fbf} = ${tp.fbf};"}.mkString("\n","\n","") else ""}
  }

  /**
   * Assembles {@code $t} value from DTO
   *
   * @param dto data transfer object
   * @param p   request projection
   *
   * @return {@code $t} value object
   */
  @Override
  public @NotNull $t.Value apply(@NotNull D dto, @NotNull ${g.shortClassName} p) {
    if (dto == null)
      return $t.type.createValue(ws.epigraph.errors.ErrorValue.NULL);
    else ${if (hasTails) tailsBuild else nonTailsBuild}
  }
}"""/*@formatter:on*/
  }
}
