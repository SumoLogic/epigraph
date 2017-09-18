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

import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenUtils.up
import ws.epigraph.java.service.projections.req.update.ReqUpdateProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, ReqFieldProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputFieldProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqUpdateFieldProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  protected val fieldName: String,
  protected val op: OpOutputFieldProjection,
  baseNamespaceOpt: Option[Qn],
  override protected val namespaceSuffix: Qn,
  dataParentClassGenOpt: Option[ReqUpdateTypeProjectionGen],
  protected val ctx: GenContext) extends ReqUpdateProjectionGen with ReqFieldProjectionGen {

  override type OpFieldProjectionType = OpOutputFieldProjection

  override protected def baseNamespace: Qn = baseNamespaceOpt.getOrElse(super.baseNamespace)

  override val shortClassName: String = s"$classNamePrefix${up(fieldName)}Field$classNameSuffix"

  override lazy val dataProjectionGen: ReqUpdateProjectionGen =
    ReqUpdateVarProjectionGen.dataProjectionGen(
      baseNamespaceProvider,
      op.varProjection(),
      baseNamespaceOpt,
      namespaceSuffix,
      dataParentClassGenOpt,
      ctx
    )

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.update.ReqUpdateFieldProjection")
  )

}
