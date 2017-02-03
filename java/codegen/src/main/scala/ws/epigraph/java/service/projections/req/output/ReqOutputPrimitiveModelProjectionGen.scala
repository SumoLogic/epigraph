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

import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen}
import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.{OpOutputPrimitiveModelProjection, OpOutputRecordModelProjection}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputPrimitiveModelProjectionGen(
  operationInfo: OperationInfo,
  protected val op: OpOutputPrimitiveModelProjection,
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputModelProjectionGen(operationInfo, op, namespaceSuffix, ctx) {

  override type OpProjectionType = OpOutputPrimitiveModelProjection

  override protected def generate: String = {
    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "ws.epigraph.projections.req.output.ReqOutputPrimitiveModelProjection",
      "ws.epigraph.projections.req.output.ReqOutputModelProjection",
      "ws.epigraph.projections.req.output.ReqOutputVarProjection"
    ) ++ params.imports ++ meta.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

$classJavadoc\
public class $shortClassName {
  private final @NotNull ReqOutputPrimitiveModelProjection raw;

  public $shortClassName(@NotNull ReqOutputModelProjection<?, ?, ?> raw) {
    this.raw = (ReqOutputPrimitiveModelProjection) raw;
  }

  public $shortClassName(@NotNull ReqOutputVarProjection selfVar) {
    this(selfVar.singleTagProjection().projection());
  }

${required.code}
${params.code}\
${meta.code}\

  public @NotNull ReqOutputPrimitiveModelProjection _raw() { return raw; }
}"""/*@formatter:on*/
  }
}
