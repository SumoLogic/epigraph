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

import ws.epigraph.compiler.{CDatumType, CType, CTypeKind}
import ws.epigraph.java.JavaGenNames.{ln, lqn2}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.output._
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class MapAssemblerGen(
  override protected val g: ReqOutputMapModelProjectionGen,
  val ctx: GenContext) extends JavaGen with ModelAssemblerGen {

  override protected type G = ReqOutputMapModelProjectionGen

  val keyCType: CDatumType = JavaGenUtils.toCType(g.op.`type`().keyType())

  val itemCType: CType = JavaGenUtils.toCType(
    g.elementGen match {
      case eg: ReqOutputVarProjectionGen => eg.op.`type`()
      case mg: ReqOutputModelProjectionGen => mg.op.`type`()
    }
  )

  private val isEntity = itemCType.kind == CTypeKind.ENTITY

  private val kt = lqn2(keyCType, nsString)
  private val it = lqn2(itemCType, nsString)

  val defaultBuild: String = /*@formatter:off*/sn"""\
AssemblerContext.Key key = new AssemblerContext.Key(dto, p);
Object visited = ctx.visited.get(key);
if (visited != null)
  return ($t.Value) visited;
else {
  $t.Builder b = $t.create();
  ctx.visited.put(key, b.asValue());
  ${g.elementGen.fullClassName} itemsProjection = p.itemsProjection();
  Map<K, I> map = itemsExtractor.apply(dto);
  for (Map.Entry<K, I> entry: map.entrySet()) {
    $kt k = keyConverter.apply(entry.getKey());
    b.put${if (isEntity) "$" else "_"}(k, itemAssembler.assemble(entry.getValue(), itemsProjection, ctx));
  }
${if (hasMeta) s"  b.setMeta(metaAssembler.assemble(dto, p.meta(), ctx));\n" else ""}\
  return b.asValue();
}
"""/*@formatter:on*/

  override protected def generate: String = {
    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "org.jetbrains.annotations.Nullable",
      "java.util.function.Function",
      "java.util.Map",
      "ws.epigraph.assembly.Assembler",
      "ws.epigraph.assembly.AssemblerContext",
      "ws.epigraph.types.Type"
    )

    val keysConverterType = s"Function<K, $kt>"
    val itemAssemblerType = s"Assembler<I, ${ g.elementGen.fullClassName }, /*@NotNull*/ $it${ if (isEntity) "" else ".Value" }>"

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package ${g.namespace};

${JavaGenUtils.generateImports(imports)}

/**
 * Value assembler for {@code ${ln(cType)}} type, driven by request output projection
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName<K, D, I> implements Assembler<@Nullable D, @NotNull ${g.shortClassName}, /*@NotNull*/ $t.Value> {
${if (hasTails) "  private final @NotNull Function<? super D, Type> typeExtractor;\n" else "" }\
  private final @NotNull $keysConverterType keyConverter;
  private final @NotNull Function<D, Map<K, I>> itemsExtractor;
  private final @NotNull $itemAssemblerType itemAssembler;
${if (hasTails) tps.map { tp => s"  private final @NotNull ${tp.assemblerType} ${tp.assembler};"}.mkString("\n  //tail assemblers\n","\n","") else "" }\
${if (hasMeta) s"  //meta assembler\n  private final @NotNull $metaAssemblerType metaAssembler;" else ""}

  /**
   * Assembler constructor
   *
${if (hasTails) s"   * @param typeExtractor data type extractor, used to determine DTO type\n" else ""}\
   * @param keyConverter key converter
   * @param itemsExtractor items extractor
   * @param itemAssembler items assembler\
${if (hasTails) tps.map { tp => s"   * @param ${tp.javadoc}"}.mkString("\n","\n","") else "" }\
${if (hasMeta) s"\n   * @param metaAssembler metadata assembler" else ""}
   */
  public $shortClassName(
${if (hasTails) s"    @NotNull Function<? super D, Type> typeExtractor,\n" else "" }\
    @NotNull $keysConverterType keyConverter,
    @NotNull Function<D, Map<K, I>> itemsExtractor,
    @NotNull $itemAssemblerType itemAssembler\
${if (hasTails) tps.map { tp => s"    @NotNull ${tp.assemblerType} ${tp.assembler}"}.mkString(",\n", ",\n", "") else ""}\
${if (hasMeta) s",\n    @NotNull $metaAssemblerType metaAssembler" else ""}
  ) {
${if (hasTails) s"    this.typeExtractor = typeExtractor;\n" else "" }\
    this.keyConverter = keyConverter;
    this.itemsExtractor = itemsExtractor;
    this.itemAssembler = itemAssembler;\
${if (hasTails) tps.map { tp => s"    this.${tp.assembler} = ${tp.assembler};"}.mkString("\n","\n","") else ""}\
${if (hasMeta) s"\n    this.metaAssembler = metaAssembler;" else ""}
  }

  /**
   * Assembles {@code $t} value from DTO
   *
   * @param dto data transfer objects
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
