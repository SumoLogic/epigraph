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

import ws.epigraph.java.JavaGenNames.ln
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.OperationInfo
import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputMapModelProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputMapModelProjectionGen(
  operationInfo: OperationInfo,
  op: OpOutputMapModelProjection,
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputModelProjectionGen(operationInfo, op, namespaceSuffix, ctx) {

  override protected def generate: String =
  /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

import org.jetbrains.annotations.NotNull;

import ws.epigraph.projections.req.output.ReqOutputMapModelProjection;

/**
 * Request output projection for {@code ${ln(cType)}} type
 */
public class $shortClassName {
  private final @NotNull ReqOutputMapModelProjection raw;

  public $shortClassName(@NotNull ReqOutputMapModelProjection raw) { this.raw = raw; }

  public @NotNull ReqOutputMapModelProjection _raw() { return raw; }
}"""/*@formatter:on*/
}
