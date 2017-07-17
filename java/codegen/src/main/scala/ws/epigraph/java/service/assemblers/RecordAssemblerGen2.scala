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
import ws.epigraph.java.service.projections.req.output.{ReqOutputFieldProjectionGen, ReqOutputProjectionGen, ReqOutputRecordModelProjectionGen}
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}

//currently unused version utilizing `RecordFieldAssemblersGen`
/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class RecordAssemblerGen2(
  override protected val g: ReqOutputRecordModelProjectionGen,
  val ctx: GenContext) extends JavaGen with ModelAssemblerGen {

  override protected type G = ReqOutputRecordModelProjectionGen

  lazy val fieldAssemblersGen: RecordFieldAssemblersGen = new RecordFieldAssemblersGen(g, ctx)

  override def children = Iterable(fieldAssemblersGen)

  case class FieldParts(field: CField, fieldGen: ReqOutputProjectionGen) {
    // todo remove unused parts
    def fieldName: String = jn(field.name)

    def fieldType: CType = field.typeRef.resolved

    def isEntity: Boolean = fieldType.kind == CTypeKind.ENTITY

    def fieldAssemblerType: String = s"Assembler<? super D, ? super ${ fieldGen.fullClassName }, ? extends ${
      lqn2(
        fieldType,
        g.namespace.toString
      )
    }${ if (isEntity) "" else ".Value" }>"

    def fbf: String = field.name + "Assembler"

    def getter: String = fieldName + "()"

    def setter: String = "set" + JavaGenUtils.up(field.name) + (if (isEntity) "" else "_")

    def dispatchFieldInit: String = s"if (p.$getter != null) b.$setter($fbf.assemble(dto, p.$getter, ctx));"

    def javadoc: String = s"$fbf {@code $fieldName} field assembler"
  }

  private def fieldGenerators(g: G): Map[String, (CField, ReqOutputFieldProjectionGen)] =
    g.parentClassGenOpt.map(pg => fieldGenerators(pg.asInstanceOf[G])).getOrElse(Map()) ++
    g.fieldGenerators.map { case (f, p) => f.name -> (f, p) }

  private val fps: Seq[FieldParts] = fieldGenerators(g).map { case (_, (f, fg)) =>
    FieldParts(f, fg.dataProjectionGen)
  }.toSeq

  protected override val defaultBuild: String = /*@formatter:off*/sn"""\
AssemblerContext.Key key = new AssemblerContext.Key(dto, p);
Object visited = ctx.visited.get(key);
if (visited != null)
  return ($t.Value) visited;
else {
  $t.Builder b = $t.create();
  ctx.visited.put(key, b.asValue());
${fps.map { fp => s"  if (p.${fp.getter} != null) b.${fp.setter}(fieldAssemblers.${fp.fieldName}().assemble(dto, p.${fp.getter}, ctx));" }.mkString("\n")}
${if (hasMeta) s"  b.setMeta(metaAssembler.assemble(dto, p.meta(), ctx));\n" else ""}\
  return b.asValue();
}
"""/*@formatter:on*/

  override protected def generate: String = {
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
 * Value assembler for {@code ${ln(cType)}} type, driven by request output projection
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName<D> implements Assembler<@Nullable D, @NotNull ${g.shortClassName}, /*@NotNull*/ $t.Value> {
${if (hasTails) "  private final @NotNull Function<? super D, Type> typeExtractor;\n" else "" }\
  private final @NotNull ${fieldAssemblersGen.shortClassName}<D> fieldAssemblers;\
${if (hasTails) tps.map { tp => s"  private final @NotNull ${tp.assemblerType} ${tp.assembler};"}.mkString("\n  //tail assemblers\n","\n","") else "" }\
${if (hasMeta) s"  //meta assembler\n  private final @NotNull $metaAssemblerType metaAssembler;" else ""}

  /**
   * Assembler constructor
   *
${if (hasTails) s"   * @param typeExtractor data type extractor, used to determine DTO type\n" else ""}\
   * @param fieldAssemblers fields assemblers\
${if (hasTails) tps.map { tp => s"   * @param ${tp.javadoc}"}.mkString("\n","\n","") else "" }\
${if (hasMeta) s"\n   * @param metaAssembler metadata assembler" else ""}
   */
  public $shortClassName(
${if (hasTails) s"    @NotNull Function<? super D, Type> typeExtractor,\n" else "" }\
    @NotNull ${fieldAssemblersGen.shortClassName}<D> fieldAssemblers\
${if (hasTails) tps.map { tp => s"    @NotNull ${tp.assemblerType} ${tp.assembler}"}.mkString(",\n", ",\n", "") else ""}\
${if (hasMeta) s",\n    @NotNull $metaAssemblerType metaAssembler" else ""}
  ) {
${if (hasTails) s"    this.typeExtractor = typeExtractor;\n" else "" }\
    this.fieldAssemblers = fieldAssemblers;\
${if (hasTails) tps.map { tp => s"    this.${tp.assembler} = ${tp.assembler};"}.mkString("\n","\n","") else ""}\
${if (hasMeta) s"\n    this.metaAssembler = metaAssembler;" else ""}
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
  public @NotNull $t.Value assemble(@NotNull D dto, @NotNull ${g.shortClassName} p, @NotNull AssemblerContext ctx) {
    if (dto == null)
      return $t.type.createValue(ws.epigraph.errors.ErrorValue.NULL);
    else ${if (hasTails) tailsBuild else nonTailsBuild}
  }
}"""/*@formatter:on*/
  }
}
