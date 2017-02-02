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

import ws.epigraph.java.JavaGenUtils.up
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen}
import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputFieldProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputFieldProjectionGen(
  operationInfo: OperationInfo,
  fieldName: String,
  op: OpOutputFieldProjection,
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputProjectionGen(operationInfo, namespaceSuffix, ctx) {

  override val shortClassName: String = s"$classNamePrefix${up(fieldName)}Field$classNameSuffix"

  override def children: Iterable[ReqProjectionGen] =
    Iterable(
      ReqOutputVarProjectionGen.dataProjectionGen(
        operationInfo,
        op.varProjection(),
        namespaceSuffix,
        ctx
      )
    )

  // todo data projection accessor

  override protected def generate: String = {
    val (params, paramImports) =
      ReqProjectionGen.generateParams(op.params(), namespace.toString, "raw.params()")

    val imports: Set[String] = paramImports ++ Set(
      "org.jetbrains.annotations.NotNull",
      "ws.epigraph.projections.req.output.ReqOutputFieldProjection"
    )

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

/**
 * Request output projection for {@code $fieldName} field
 */
public class $shortClassName {
  private final @NotNull ReqOutputFieldProjection raw;

  public $shortClassName(@NotNull ReqOutputFieldProjection raw) { this.raw = raw; }
$params\

  public @NotNull ReqOutputFieldProjection _raw() { return raw; }
}"""/*@formatter:on*/
  }

}

