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

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class RecordAssemblerGen(
  override protected val g: ReqOutputRecordModelProjectionGen,
  val ctx: GenContext) extends JavaGen with ModelAssemblerGen {

  override protected type G = ReqOutputRecordModelProjectionGen

  import Imports._

  case class FieldParts(field: CField, fieldGen: ReqOutputProjectionGen) {
    def fieldName: String = jn(field.name)

    def fieldType: CType = field.typeRef.resolved

    def isEntity: Boolean = fieldType.kind == CTypeKind.ENTITY

    val fieldGenType: importManager.ImportedName = importManager.use(fieldGen.fullClassName)

    val assemblerResultType: importManager.ImportedName = importManager.use(
      lqn2(
        fieldType,
        g.namespace.toString
      )
    )

    def fieldAssemblerType: String = s"$assembler<? super D, ? super $fieldGenType, ? extends $assemblerResultType${ if (isEntity) "" else ".Value" }>"

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

  private val obj = importManager.use("java.lang.Object")

  protected override lazy val defaultBuild: String = {
    /*@formatter:off*/sn"""\
$assemblerContext.Key key = new $assemblerContext.Key(dto, p);
$obj visited = ctx.visited.get(key);
if (visited != null)
  return ($t.Value) visited;
else {
  $t.Builder b = $t.create();
  ctx.visited.put(key, b.asValue());
${fps.map { fp => s"  if (p.${fp.getter} != null) b.${fp.setter}(${fp.fbf}.assemble(dto, p.${fp.getter}, ctx));" }.mkString("\n")}
${if (hasMeta) s"  b.setMeta(metaAssembler.assemble(dto, p.meta(), ctx));\n" else ""}\
  return b.asValue();
}
"""/*@formatter:on*/
  }

  override protected def generate: String = {
    closeImports()

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package ${g.namespace};

${JavaGenUtils.generateImports(importManager.imports)}

/**
 * Value assembler for {@code ${ln(cType)}} type, driven by request output projection
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName<D> implements $assembler<@$nullable D, @$notNull $projectionName, /*@$notNull*/ $t.Value> {
${if (hasTails) s"  private final @$notNull $func<? super D, Type> typeExtractor;\n" else "" }\
  //field assemblers
${fps.map { fp => s"  private final @$notNull ${fp.fieldAssemblerType} ${fp.fbf};"}.mkString("\n") }\
${if (hasTails) tps.map { tp => s"  private final @$notNull ${tp.assemblerType} ${tp.assembler};"}.mkString("\n  //tail assemblers\n","\n","") else "" }\
${if (hasMeta) s"  //meta assembler\n  private final @$notNull $metaAssemblerType metaAssembler;" else ""}

  /**
   * Assembler constructor
   *
${if (hasTails) s"   * @param typeExtractor data type extractor, used to determine DTO type\n" else ""}\
${fps.map { fp => s"   * @param ${fp.javadoc}"}.mkString("\n") }\
${if (hasTails) tps.map { tp => s"   * @param ${tp.javadoc}"}.mkString("\n","\n","") else "" }\
${if (hasMeta) s"\n   * @param metaAssembler metadata assembler" else ""}
   */
  public $shortClassName(
${if (hasTails) s"    @$notNull $func<? super D, Type> typeExtractor,\n" else "" }\
${fps.map { fp => s"    @$notNull ${fp.fieldAssemblerType} ${fp.fbf}"}.mkString(",\n") }\
${if (hasTails) tps.map { tp => s"    @$notNull ${tp.assemblerType} ${tp.assembler}"}.mkString(",\n", ",\n", "") else ""}\
${if (hasMeta) s",\n    @$notNull $metaAssemblerType metaAssembler" else ""}
  ) {
${if (hasTails) s"    this.typeExtractor = typeExtractor;\n" else "" }\
${fps.map { fp => s"    this.${fp.fbf} = ${fp.fbf};"}.mkString("\n") }\
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
  public @$notNull $t.Value assemble(@$notNull D dto, @$notNull $projectionName p, @$notNull $assemblerContext ctx) {
    if (dto == null)
      return $t.type.createValue($errValue.NULL);
    else ${if (hasTails) tailsBuild else nonTailsBuild}
  }
}"""/*@formatter:on*/
  }
}
