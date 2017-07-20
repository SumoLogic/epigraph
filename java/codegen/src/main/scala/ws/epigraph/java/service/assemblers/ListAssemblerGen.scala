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

import ws.epigraph.compiler.{CType, CTypeKind}
import ws.epigraph.java.JavaGenNames.{ln, lqn2}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.output._
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ListAssemblerGen(
  override protected val g: ReqOutputListModelProjectionGen,
  val ctx: GenContext) extends JavaGen with ModelAssemblerGen {

  override protected type G = ReqOutputListModelProjectionGen

  val itemCType: CType = JavaGenUtils.toCType(
    g.elementGen match {
      case eg: ReqOutputVarProjectionGen => eg.op.`type`()
      case mg: ReqOutputModelProjectionGen => mg.op.`type`()
    }
  )

  private val isEntity = itemCType.kind == CTypeKind.ENTITY

  private val elementGenName = importManager.use(g.elementGen.fullClassName)

  import Imports._

  private val obj = importManager.use("java.lang.Object")
  private val iterable = importManager.use("java.lang.Iterable")

  override protected lazy val defaultBuild: String = {
    /*@formatter:off*/sn"""\
$assemblerContext.Key key = new $assemblerContext.Key(dto, p);
$obj visited = ctx.visited.get(key);
if (visited != null)
  return ($t.Value) visited;
else {
  $t.Builder b = $t.create();
  ctx.visited.put(key, b.asValue());
  $elementGenName itemsProjection = p.itemsProjection();
  $iterable<? extends I> items = itemsExtractor.apply(dto);
  for (I item: items) {
    b.add${if (isEntity) "" else "_"}(itemAssembler.assemble(item, itemsProjection, ctx));
  }
${if (hasMeta) s"  b.setMeta(metaAssembler.assemble(dto, p.meta(), ctx));\n" else ""}\
  return b.asValue();
}
"""/*@formatter:on*/
  }

  override protected def generate: String = {
    val it = importManager.use(lqn2(itemCType, nsString))

    closeImports()

    val itemAssemblerType = s"$assembler<I, $elementGenName, /*$notNull*/ $it${ if (isEntity) "" else ".Value" }>"

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package ${g.namespace};

${JavaGenUtils.generateImports(importManager.imports)}

/**
 * Value assembler for {@code ${ln(cType)}} type, driven by request output projection
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName<D, I> implements $assembler<D, $notNull $projectionName, $notNull $t.Value> {
${if (hasTails) s"  private final $notNull $func<? super D, ? extends Type> typeExtractor;\n" else "" }\
  private final $notNull $func<D, $iterable<? extends I>> itemsExtractor;
  private final $notNull $itemAssemblerType itemAssembler;
${if (hasTails) tps.map { tp => s"  private final $notNull ${tp.assemblerType} ${tp.assembler};"}.mkString("\n  //tail assemblers\n","\n","") else "" }\
${if (hasMeta) s"  //meta assembler\n  private final $notNull $metaAssemblerType metaAssembler;" else ""}

  /**
   * Assembler constructor
   *
${if (hasTails) s"   * @param typeExtractor data type extractor, used to determine DTO type\n" else ""}\
   * @param itemsExtractor items extractor
   * @param itemAssembler items assembler\
${if (hasTails) tps.map { tp => s"   * @param ${tp.javadoc}"}.mkString("\n","\n","") else "" }\
${if (hasMeta) s"\n   * @param metaAssembler metadata assembler" else ""}
   */
  public $shortClassName(
${if (hasTails) s"    $notNull $func<? super D, ? extends Type> typeExtractor,\n" else "" }\
    $notNull $func<D, $iterable<? extends I>> itemsExtractor,
    $notNull $itemAssemblerType itemAssembler\
${if (hasTails) tps.map { tp => s"    $notNull ${tp.assemblerType} ${tp.assembler}"}.mkString(",\n", ",\n", "") else ""}\
${if (hasMeta) s",\n    $notNull $metaAssemblerType metaAssembler" else ""}
  ) {
${if (hasTails) s"    this.typeExtractor = typeExtractor;\n" else "" }\
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
  public $notNull $t.Value assemble(D dto, $notNull $projectionName p, $notNull $assemblerContext ctx) {
    if (dto == null)
      return $t.type.createValue($errValue.NULL);
    else ${if (hasTails) tailsBuild else nonTailsBuild}
  }
}"""/*@formatter:on*/
  }
}
