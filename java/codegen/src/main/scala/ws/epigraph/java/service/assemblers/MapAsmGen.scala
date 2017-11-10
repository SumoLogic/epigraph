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
import ws.epigraph.java.service.projections.req.output.{ReqOutputEntityProjectionGen, ReqOutputMapModelProjectionGen}
import ws.epigraph.java.service.projections.req.{ReqEntityProjectionGen, ReqModelProjectionGen}
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}
import ws.epigraph.util.HttpStatusCode

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class MapAsmGen(
  override protected val g: ReqOutputMapModelProjectionGen,
  val ctx: GenContext) extends JavaGen with ModelAsmGen {

  override protected type G = ReqOutputMapModelProjectionGen

  val keyCType: CDatumType = JavaGenUtils.toCType(g.op.`type`().keyType())

  val itemCType: CType = JavaGenUtils.toCType(
    g.elementGen match {
      case eg: ReqEntityProjectionGen => eg.op.`type`()
      case mg: ReqModelProjectionGen => mg.op.`type`()
    }
  )

  private val isEntity = itemCType.kind == CTypeKind.ENTITY

  private val kt = importManager.use(lqn2(keyCType, nsString))
  private val it = importManager.use(lqn2(itemCType, nsString))
  private val keyGenName = importManager.use(g.keyGen.fullClassName)
  private val elementGenName = importManager.use(g.elementGen.fullClassName)
  private val fun = importManager.use(classOf[java.util.function.Function[_, _]].getName)
  private val fun2 = importManager.use(classOf[ws.epigraph.util.Function2[_, _, _]].getName)
  private val listImp = importManager.use(classOf[java.util.List[_]].getName)
  private val mapImp = importManager.use(classOf[java.util.Map[_, _]].getName)
  private val collectorsImp = importManager.use(classOf[java.util.stream.Collectors].getName)
  private val statusCodeImp = importManager.use(classOf[HttpStatusCode].getName)

  import Imports._

  private val obj = importManager.use("java.lang.Object")
  private val mp = importManager.use("java.util.Map")

  private val vp = if (isEntity) "" else ".Value"
  private val pp = if (isEntity) "$" else "_"

  private def datumNotFound = /*@formatter:off*/ sn"""\
        value = $it.type.createValue(new $errValue($statusCodeImp.NOT_FOUND, "item not found"));\
"""/*@formatter:on*/

  private def dataNotFound = {
    val eeg: ReqOutputEntityProjectionGen = g.elementGen.asInstanceOf[ReqOutputEntityProjectionGen]

    def tagError(tagName: String) = /*@formatter:off*/ sn"""\
        if (itemsProjection.${eeg.tagMethodName(tagName)}() != null)
          builder.set${JavaGenUtils.up(tagName)}_Error(error);\
"""/*@formatter:on*/

    /*@formatter:off*/ sn"""\
        $errValue error = new $errValue($statusCodeImp.NOT_FOUND, "object not found");
        $it.Builder builder = $it.create();
${ eeg.tagProjections.keys.map (tagError).mkString("", "\n", "\n") }\
        value = builder;\
"""/*@formatter:on*/
  }

  private val builtInPrimitiveKey = JavaGenUtils.builtInPrimitives.contains(keyCType.name.name.toString)
  private val keyValueSuffix = if (isEntity && builtInPrimitiveKey) ".getVal()" else ""

  protected lazy val defaultBuild: String = {
    /*@formatter:off*/sn"""\
$asmCtx.Key key = new $asmCtx.Key(dto, p);
$obj visited = ctx.visited.get(key);
if (visited != null)
  return ($t.Value) visited;
else {
  $t.Builder b = $t.create();
  ctx.visited.put(key, b.asValue());
  $elementGenName itemsProjection = p.itemsProjection();
  $mp<K, ? extends V> map = mapExtractor.apply(dto, p);
  $listImp<$keyGenName> keys = p.keys();
  if (keys == null) {
    for ($mp.Entry<K, ? extends V> entry: map.entrySet()) {
      $kt k = keyConverter.apply(entry.getKey());
      b.put$pp(k$keyValueSuffix, itemAsm.assemble(entry.getValue(), itemsProjection, ctx));
    }
  } else {
    $mapImp<$kt, K> revIndex = map.keySet().stream().collect($collectorsImp.toMap(keyConverter::apply, $fun.identity()));
    for ($keyGenName keyProjection : keys) {
      $kt k = keyProjection.value${if (builtInPrimitiveKey) "_" else ""}();
      K k2 = revIndex.get(k);
      final $it$vp value;
      if (k2 == null) {
${if (isEntity) dataNotFound else datumNotFound}
      } else {
        value = itemAsm.assemble(map.get(k2), itemsProjection, ctx);
      }
      b.put$pp(k$keyValueSuffix, value);
    }
  }
${if (hasMeta) s"  b.setMeta(metaAsm.assemble(dto, p.meta(), ctx));\n" else ""}\
  return b.asValue();
}
"""/*@formatter:on*/
  }

  override protected def generate: String = {
    closeImports()

    val keysConverterType = s"$func<K, $kt>"
    val itemAsmType = s"$asm<V, $elementGenName, /*$notNull*/ $it$vp>"

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package ${g.namespace};

${JavaGenUtils.generateImports(importManager.imports)}

/**
 * Value assembler for {@code ${ln(cType)}} type, driven by request output projection
 *
 * @param <D> DTO type, should be decomposable into a map from {@code K} to {@code V}
 * @param <K> DTO map keys type
 * @param <V> DTO map keys type
 */
${JavaGenUtils.generatedAnnotation(this)}
public class $shortClassName<D, K, V> implements $asm<D, $notNull $projectionName, $notNull $t.Value> {
${if (hasTails) s"  private final $notNull $func<? super D, ? extends Type> typeExtractor;\n" else "" }\
  private final $notNull $fun2<D, $projectionName, ? extends $mp<K, ? extends V>> mapExtractor;
  private final $notNull $keysConverterType keyConverter;
  private final $notNull $itemAsmType itemAsm;
${if (hasTails) tps.map { tp => s"  private final $notNull ${tp.assemblerType} ${tp.assembler};"}.mkString("\n  //tail assemblers\n","\n","") else "" }\
${if (hasMeta) s"  //meta assembler\n  private final $notNull $metaAsmType metaAsm;" else ""}

  /**
   * Asm constructor
   *
${if (hasTails) s"   * @param typeExtractor data type extractor, used to determine DTO type\n" else ""}\
   * @param mapExtractor map extractor
   * @param keyConverter key converter
   * @param itemAsm items assembler\
${if (hasTails) tps.map { tp => s"   * @param ${tp.javadoc}"}.mkString("\n","\n","") else "" }\
${if (hasMeta) s"\n   * @param metaAsm metadata assembler" else ""}
   */
  public $shortClassName(
${if (hasTails) s"    $notNull $func<? super D, ? extends Type> typeExtractor,\n" else "" }\
    $notNull $fun2<D, $projectionName, ? extends $mp<K, ? extends V>> mapExtractor,
    $notNull $keysConverterType keyConverter,
    $notNull $itemAsmType itemAsm\
${if (hasTails) tps.map { tp => s"    $notNull ${tp.assemblerType} ${tp.assembler}"}.mkString(",\n", ",\n", "") else ""}\
${if (hasMeta) s",\n    $notNull $metaAsmType metaAsm" else ""}
  ) {
${if (hasTails) s"    this.typeExtractor = typeExtractor;\n" else "" }\
    this.mapExtractor = mapExtractor;
    this.keyConverter = keyConverter;
    this.itemAsm = itemAsm;\
${if (hasTails) tps.map { tp => s"    this.${tp.assembler} = ${tp.assembler};"}.mkString("\n","\n","") else ""}\
${if (hasMeta) s"\n    this.metaAsm = metaAsm;" else ""}
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
  public $notNull $t.Value assemble(D dto, $notNull $projectionName p, $notNull $asmCtx ctx) {
    if (dto == null)
      return $t.type.createValue($errValue.NULL);
    else ${if (hasTails) tailsBuild else nonTailsBuild}
  }
}"""/*@formatter:on*/
  }
}
