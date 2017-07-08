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
import ws.epigraph.java.service.assemblers.MapAssemblerGen
import ws.epigraph.java.{GenContext, JavaGen}
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, ReqMapModelProjectionGen, ReqModelProjectionGen, ReqProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.OpKeyPresence
import ws.epigraph.projections.op.output.OpOutputMapModelProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputMapModelProjectionGen(
  baseNamespaceProvider: BaseNamespaceProvider,
  override val op: OpOutputMapModelProjection,
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  override protected val parentClassGenOpt: Option[ReqProjectionGen],
  ctx: GenContext)
  extends ReqOutputModelProjectionGen(baseNamespaceProvider, op, baseNamespaceOpt, _namespaceSuffix, parentClassGenOpt, ctx) with ReqMapModelProjectionGen {

  override type OpProjectionType = OpOutputMapModelProjection

  override protected def keysNullable: Boolean = op.keyProjection().presence() != OpKeyPresence.REQUIRED

  override val keyGen: ReqOutputMapKeyProjectionGen = new ReqOutputMapKeyProjectionGen(
    baseNamespaceProvider,
    cType.asInstanceOf[CMapType],
    op.keyProjection(),
    Some(baseNamespace),
    namespaceSuffix,
    ctx
  )

  override val elementGen: ReqOutputProjectionGen = ReqOutputVarProjectionGen.dataProjectionGen(
    baseNamespaceProvider,
    op.itemsProjection(),
    Some(baseNamespace),
    namespaceSuffix.append(elementsNamespaceSuffix),
    None, // todo should extend parent element projection?
    ctx
  )

  override protected def tailGenerator(parentGen: ReqModelProjectionGen, op: OpOutputMapModelProjection, normalized: Boolean) =
    new ReqOutputMapModelProjectionGen(
      baseNamespaceProvider,
      op,
      Some(baseNamespace),
      tailNamespaceSuffix(op.`type`(), normalized),
      Some(parentGen),
      ctx
    ) {
      override protected val buildTails: Boolean = !normalized
      override protected val buildNormalizedTails: Boolean = normalized
    }


  override def children: Iterable[JavaGen] = super.children ++ Iterable(new MapAssemblerGen(this, ctx))

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.output.ReqOutputMapModelProjection"),
    required
  )
}
