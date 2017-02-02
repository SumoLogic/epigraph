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

import ws.epigraph.compiler.CListType
import ws.epigraph.java.JavaGenNames.ln
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen}
import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputListModelProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputListModelProjectionGen(
  operationInfo: OperationInfo,
  op: OpOutputListModelProjection,
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputModelProjectionGen(operationInfo, op, namespaceSuffix, ctx) {

  private val cListType = cType.asInstanceOf[CListType]

  private val elementsNamespaceSuffix = namespaceSuffix.append("elements")

  private val elementGen = ReqOutputVarProjectionGen.dataProjectionGen(
    operationInfo,
    op.itemsProjection(),
    elementsNamespaceSuffix,
    ctx
  )

  override def children: Iterable[ReqProjectionGen] = Iterable(elementGen)

  override protected def generate: String = {
    val elementProjectionClass = elementGen.shortClassName

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "ws.epigraph.projections.req.output.ReqOutputListModelProjection",
      "ws.epigraph.projections.req.output.ReqOutputModelProjection",
      "ws.epigraph.projections.req.output.ReqOutputVarProjection",
      elementGen.fullClassName
    ) ++ params.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

/**
 * Request output projection for {@code ${ln(cType)}} type
 */
public class $shortClassName {
  private final @NotNull ReqOutputListModelProjection raw;

  public $shortClassName(@NotNull ReqOutputModelProjection<?, ?, ?> raw) {
    this.raw = (ReqOutputListModelProjection) raw;
  }

  public $shortClassName(@NotNull ReqOutputVarProjection selfVar) {
    this(selfVar.singleTagProjection().projection());
  }

${required.code}\

  /**
   * @return items projection
   */
  public @NotNull $elementProjectionClass itemsProjection() {
    return new $elementProjectionClass(raw.itemsProjection());
  }
${params.code}\

  public @NotNull ReqOutputListModelProjection _raw() { return raw; }
}"""/*@formatter:on*/
  }
}