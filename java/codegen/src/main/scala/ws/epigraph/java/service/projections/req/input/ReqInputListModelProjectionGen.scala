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

package ws.epigraph.java.service.projections.req.input

import ws.epigraph.java.GenContext
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, ReqListModelProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.OpListModelProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqInputListModelProjectionGen(
  baseNamespaceProvider: BaseNamespaceProvider,
  val op: OpListModelProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override val parentClassGenOpt: Option[ReqInputModelProjectionGen],
  ctx: GenContext)
  extends ReqInputModelProjectionGen(
    baseNamespaceProvider,
    op,
    baseNamespaceOpt,
    _namespaceSuffix,
    parentClassGenOpt,
    ctx
  ) with ReqListModelProjectionGen {

  override type OpProjectionType = OpListModelProjection

  val elementGen: ReqInputTypeProjectionGen = ReqInputEntityProjectionGen.dataProjectionGen(
    baseNamespaceProvider,
    op.itemsProjection(),
    Some(baseNamespace),
    namespaceSuffix.append(elementsNamespaceSuffix),
    parentClassGenOpt match {
      case Some(lmpg: ReqInputListModelProjectionGen) => Some(lmpg.elementGen)
      case _ => None
    },
    ctx
  )

  override protected def tailGenerator(
    parentGen: ReqInputModelProjectionGen,
    op: OpListModelProjection,
    normalized: Boolean) =
    new ReqInputListModelProjectionGen(
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

//  override protected def generate: String = generate(
//    Qn.fromDotSeparated("ws.epigraph.projections.req.input.ReqInputListModelProjection")
//  )

}
