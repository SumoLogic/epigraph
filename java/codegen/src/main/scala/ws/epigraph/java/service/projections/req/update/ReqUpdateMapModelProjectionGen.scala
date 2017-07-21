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
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, ReqMapModelProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.OpKeyPresence
import ws.epigraph.projections.op.input.OpInputMapModelProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqUpdateMapModelProjectionGen(
  baseNamespaceProvider: BaseNamespaceProvider,
  override val op: OpInputMapModelProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override protected val parentClassGenOpt: Option[ReqUpdateModelProjectionGen],
  ctx: GenContext)
  extends ReqUpdateModelProjectionGen(
    baseNamespaceProvider,
    op,
    baseNamespaceOpt,
    _namespaceSuffix,
    parentClassGenOpt,
    ctx
  ) with ReqMapModelProjectionGen {

  override type OpProjectionType = OpInputMapModelProjection

  override protected def keysNullable: Boolean = op.keyProjection().presence() != OpKeyPresence.REQUIRED

  override val keyGen: ReqUpdateMapKeyProjectionGen = new ReqUpdateMapKeyProjectionGen(
    baseNamespaceProvider,
    cType.asInstanceOf[CMapType],
    op.keyProjection(),
    Some(baseNamespace),
    namespaceSuffix,
    ctx
  )

  override val elementGen: ReqUpdateTypeProjectionGen = ReqUpdateVarProjectionGen.dataProjectionGen(
    baseNamespaceProvider,
    op.itemsProjection(),
    Some(baseNamespace),
    namespaceSuffix.append(elementsNamespaceSuffix),
    parentClassGenOpt match {
      case Some(mmpg: ReqUpdateMapModelProjectionGen) => Some(mmpg.elementGen)
      case _ => None
    },
    ctx
  )

  override protected def tailGenerator(
    parentGen: ReqUpdateModelProjectionGen,
    op: OpInputMapModelProjection,
    normalized: Boolean) =
    new ReqUpdateMapModelProjectionGen(
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      Some(parentGen),
      ctx
    )
//    {
//      override protected val buildTails: Boolean = !normalized
//      override protected val buildNormalizedTails: Boolean = normalized
//    }

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.update.ReqUpdateMapModelProjection"),
    replace
  )
}
