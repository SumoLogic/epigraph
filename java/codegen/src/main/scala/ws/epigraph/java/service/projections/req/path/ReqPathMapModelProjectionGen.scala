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

package ws.epigraph.java.service.projections.req.path

import ws.epigraph.compiler.CMapType
import ws.epigraph.java.GenContext
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.{CodeChunk, OperationInfo, ReqMapModelProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.path.OpMapModelPath

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqPathMapModelProjectionGen(
  operationInfo: OperationInfo,
  override protected val op: OpMapModelPath,
  namespaceSuffix: Qn,
  ctx: GenContext)
  extends ReqPathModelProjectionGen(operationInfo, op, namespaceSuffix, ctx) with ReqMapModelProjectionGen {

  override type OpProjectionType = OpMapModelPath

  protected override val keyGen: ReqPathMapKeyProjectionGen = new ReqPathMapKeyProjectionGen(
    operationInfo,
    cType.asInstanceOf[CMapType],
    op.keyProjection(),
    namespaceSuffix,
    ctx
  )

  protected override val elementGen: ReqPathProjectionGen = ReqPathVarProjectionGen.dataProjectionGen(
    operationInfo,
    op.itemsProjection(),
    namespaceSuffix.append(elementsNamespaceSuffix),
    ctx
  )

  override protected def keys: CodeChunk = {
    val keyProjectionClass = keyGen.shortClassName

    CodeChunk(
      /*@formatter:off*/sn"""\
  /**
   * @return key projection
   */
  public @NotNull $keyProjectionClass key() {
    return new $keyProjectionClass(raw.key());
  }
"""/*@formatter:on*/
      ,
      Set("org.jetbrains.annotations.NotNull")
    )
  }

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqMapModelPath")
  )
}
