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
import ws.epigraph.java.service.projections.req.path.ReqPathProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{BaseNamespaceProvider, ReqMapKeyProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.OpKeyProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqPathMapKeyProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  protected val cMapType: CMapType,
  protected val op: OpKeyProjection,
  baseNamespaceOpt: Option[Qn],
  override protected val namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqPathProjectionGen with ReqMapKeyProjectionGen {

  override type OpKeyProjectionType = OpKeyProjection

  override protected def baseNamespace: Qn = baseNamespaceOpt.getOrElse(super.baseNamespace)

  override def shortClassName: String = s"$classNamePrefix${mapTypeShortName}Key$classNameSuffix"

//  override protected def generate: String = generate(
//    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqPathKeyProjection")
//  )
}
