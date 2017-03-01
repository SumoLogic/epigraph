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

package ws.epigraph.java.service.projections.req.update

import ws.epigraph.compiler.CMapType
import ws.epigraph.java.GenContext
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.{CodeChunk, OperationInfo, ReqMapModelProjectionGen, ReqModelProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.OpKeyPresence
import ws.epigraph.projections.op.input.OpInputMapModelProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqUpdateMapModelProjectionGen(
  operationInfo: OperationInfo,
  override protected val op: OpInputMapModelProjection,
  namespaceSuffix: Qn,
  ctx: GenContext)
  extends ReqUpdateModelProjectionGen(operationInfo, op, namespaceSuffix, ctx) with ReqMapModelProjectionGen {

  override type OpProjectionType = OpInputMapModelProjection

  override protected def keysNullable: Boolean = op.keyProjection().presence() != OpKeyPresence.REQUIRED

  protected override val keyGen: ReqUpdateMapKeyProjectionGen = new ReqUpdateMapKeyProjectionGen(
    operationInfo,
    cType.asInstanceOf[CMapType],
    op.keyProjection(),
    namespaceSuffix,
    ctx
  )

  protected override val elementGen: ReqUpdateProjectionGen = ReqUpdateVarProjectionGen.dataProjectionGen(
    operationInfo,
    op.itemsProjection(),
    namespaceSuffix.append(elementsNamespaceSuffix),
    ctx
  )

  override protected def tailGenerator(
    op: OpInputMapModelProjection,
    normalized: Boolean): ReqModelProjectionGen =
    new ReqUpdateMapModelProjectionGen(
      operationInfo,
      op,
      namespaceSuffix.append(
        ReqModelProjectionGen.typeNameToPackageName(cType, namespace.toString)
        + ReqModelProjectionGen.tailPackageSuffix(normalized)
      ),
      ctx
    ) {
      override protected lazy val normalizedTailGenerators: Map[OpInputMapModelProjection, ReqModelProjectionGen] = Map()
    }

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.update.ReqUpdateMapModelProjection"),
    update + CodeChunk(/*@formatter:off*/sn"""\
  public boolean updateKeys() {
    return raw.updateKeys();
  }
"""/*@formatter:on*/
    )
  )
}
