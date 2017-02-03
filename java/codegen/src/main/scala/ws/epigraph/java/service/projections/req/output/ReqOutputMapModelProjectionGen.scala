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

package ws.epigraph.java.service.projections.req.output

import ws.epigraph.compiler.CMapType
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen}
import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputMapModelProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputMapModelProjectionGen(
  operationInfo: OperationInfo,
  override protected val op: OpOutputMapModelProjection,
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputModelProjectionGen(operationInfo, op, namespaceSuffix, ctx) {

  override type OpProjectionType = OpOutputMapModelProjection

  private val cMapType = cType.asInstanceOf[CMapType]

  private val elementsNamespaceSuffix = namespaceSuffix.append("elements")

  private val keyGen = new ReqOutputMapKeyProjectionGen(
    operationInfo,
    cMapType,
    op.keyProjection(),
    namespaceSuffix,
    ctx
  )

  private val elementGen = ReqOutputVarProjectionGen.dataProjectionGen(
    operationInfo,
    op.itemsProjection(),
    elementsNamespaceSuffix,
    ctx
  )

  override def children: Iterable[ReqProjectionGen] = super.children ++ Iterable(keyGen, elementGen)

  override protected def generate: String = {
    val keyProjectionClass = keyGen.shortClassName
    val elementProjectionClass = elementGen.shortClassName

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "org.jetbrains.annotations.Nullable",
      "java.util.List",
      "java.util.stream.Collectors",
      "ws.epigraph.projections.req.output.ReqOutputMapModelProjection",
      "ws.epigraph.projections.req.output.ReqOutputModelProjection",
      "ws.epigraph.projections.req.output.ReqOutputVarProjection",
      elementGen.fullClassName
    ) ++ params.imports ++ meta.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

$classJavadoc\
public class $shortClassName {
  private final @NotNull ReqOutputMapModelProjection raw;

  public $shortClassName(@NotNull ReqOutputModelProjection<?, ?, ?> raw) {
    this.raw = (ReqOutputMapModelProjection) raw;
  }

  public $shortClassName(@NotNull ReqOutputVarProjection selfVar) {
    this(selfVar.singleTagProjection().projection());
  }

${required.code}
  /**
   * @return key projections
   */
  public @Nullable List<$keyProjectionClass> keys() {
    return raw.keys() == null ? null : raw.keys().stream().map(key -> new $keyProjectionClass(key)).collect(Collectors.toList());
  }

  /**
   * @return items projection
   */
  public @NotNull $elementProjectionClass itemsProjection() {
    return new $elementProjectionClass(raw.itemsProjection());
  }
${params.code}\
${meta.code}\

  public @NotNull ReqOutputMapModelProjection _raw() { return raw; }
}"""/*@formatter:on*/
  }
}
