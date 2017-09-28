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

import ws.epigraph.compiler.{CField, CType, CTypeKind}
import ws.epigraph.java.JavaGenNames.{jn, ln, lqn2}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.output.ReqOutputRecordModelProjectionGen
import ws.epigraph.java.service.projections.req.{ReqFieldProjectionGen, ReqProjectionGen}
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}

//currently unused version utilizing `RecordFieldAsmsGen`
/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class RecordAsmGen2(
  override protected val g: ReqOutputRecordModelProjectionGen,
  val ctx: GenContext) extends JavaGen with ModelAsmGen {

  override protected type G = ReqOutputRecordModelProjectionGen

  lazy val fieldAsmsGen: RecordFieldAsmsGen = new RecordFieldAsmsGen(g, ctx)

  override def children = Iterable(fieldAsmsGen)

  case class FieldParts(field: CField, fieldGen: ReqProjectionGen) {
    // todo remove unused parts
    def fieldName: String = jn(field.name)

    def fieldType: CType = field.typeRef.resolved

    def isEntity: Boolean = fieldType.kind == CTypeKind.ENTITY

    def fieldAsmType: String = s"Asm<? super D, ? super ${ fieldGen.fullClassName }, ? extends ${
      lqn2(
        fieldType,
        g.namespace.toString
      )
    }${ if (isEntity) "" else ".Value" }>"

    def fbf: String = field.name + "Asm"

    def getter: String = fieldName + "()"

    def setter: String = "set" + JavaGenUtils.up(field.name) + (if (isEntity) "" else "_")

    def dispatchFieldInit: String = s"if (p.$getter != null) b.$setter($fbf.assemble(dto, p.$getter, ctx));"

    def javadoc: String = s"$fbf {@code $fieldName} field assembler"
  }

  private def fieldGenerators(g: G): Map[String, (CField, ReqFieldProjectionGen)] =
    g.parentClassGenOpt.map(pg => fieldGenerators(pg.asInstanceOf[G])).getOrElse(Map()) ++
    g.fieldGenerators.map { case (f, p) => f.name -> (f, p) }

  private val fps: Seq[FieldParts] = fieldGenerators(g).map { case (_, (f, fg)) =>
    FieldParts(f, fg.dataProjectionGen)
  }.toSeq

  protected override val defaultBuild: String = /*@formatter:off*/sn"""\
AsmContext.Key key = new AsmContext.Key(dto, p);
Object visited = ctx.visited.get(key);
if (visited != null)
  return ($t.Value) visited;
else {
  $t.Builder b = $t.create();
  ctx.visited.put(key, b.asValue());
${fps.map { fp => s"  if (p.${fp.getter} != null) b.${fp.setter}(fieldAsms.${fp.fieldName}().assemble(dto, p.${fp.getter}, ctx));" }.mkString("\n")}
${if (hasMeta) s"  b.setMeta(metaAsm.assemble(dto, p.meta(), ctx));\n" else ""}\
  return b.asValue();
}
"""/*@formatter:on*/

  override protected def generate: String = {
    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "org.jetbrains.annotations.Nullable",
      "java.util.function.Function",
      "ws.epigraph.assembly.Asm",
      "ws.epigraph.assembly.AsmContext",
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
public class $shortClassName<D> implements Asm<@Nullable D, @NotNull ${g.shortClassName}, /*@NotNull*/ $t.Value> {
${if (hasTails) "  private final @NotNull Function<? super D, Type> typeExtractor;\n" else "" }\
  private final @NotNull ${fieldAsmsGen.shortClassName}<D> fieldAsms;\
${if (hasTails) tps.map { tp => s"  private final @NotNull ${tp.assemblerType} ${tp.assembler};"}.mkString("\n  //tail assemblers\n","\n","") else "" }\
${if (hasMeta) s"  //meta assembler\n  private final @NotNull $metaAsmType metaAsm;" else ""}

  /**
   * Asm constructor
   *
${if (hasTails) s"   * @param typeExtractor data type extractor, used to determine DTO type\n" else ""}\
   * @param fieldAsms fields assemblers\
${if (hasTails) tps.map { tp => s"   * @param ${tp.javadoc}"}.mkString("\n","\n","") else "" }\
${if (hasMeta) s"\n   * @param metaAsm metadata assembler" else ""}
   */
  public $shortClassName(
${if (hasTails) s"    @NotNull Function<? super D, Type> typeExtractor,\n" else "" }\
    @NotNull ${fieldAsmsGen.shortClassName}<D> fieldAsms\
${if (hasTails) tps.map { tp => s"    @NotNull ${tp.assemblerType} ${tp.assembler}"}.mkString(",\n", ",\n", "") else ""}\
${if (hasMeta) s",\n    @NotNull $metaAsmType metaAsm" else ""}
  ) {
${if (hasTails) s"    this.typeExtractor = typeExtractor;\n" else "" }\
    this.fieldAsms = fieldAsms;\
${if (hasTails) tps.map { tp => s"    this.${tp.assembler} = ${tp.assembler};"}.mkString("\n","\n","") else ""}\
${if (hasMeta) s"\n    this.metaAsm = metaAsm;" else ""}
  }

  /**
   * Assembles {@code $t} value from DTO
   *
   * @param dto data transfer object
   * @param p   request projection
   * @param ctx assembly context
   *
   * @return {@code $t} value object
   */
  @Override
  public @NotNull $t.Value assemble(@NotNull D dto, @NotNull ${g.shortClassName} p, @NotNull AsmContext ctx) {
    if (dto == null)
      return $t.type.createValue(ws.epigraph.errors.ErrorValue.NULL);
    else ${if (hasTails) tailsBuild else nonTailsBuild}
  }
}"""/*@formatter:on*/
  }
}
