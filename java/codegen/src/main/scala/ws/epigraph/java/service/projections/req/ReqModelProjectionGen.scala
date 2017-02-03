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

package ws.epigraph.java.service.projections.req

import ws.epigraph.compiler.CDatumType
import ws.epigraph.java.JavaGenNames.{ln, lqn2}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.AbstractOpModelProjection
import ws.epigraph.types.DatumTypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqModelProjectionGen extends ReqProjectionGen {
  type OpProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi]

  protected def op: OpProjectionType

  protected val cType: CDatumType = ReqProjectionGen.toCType(op.model())

  protected def reqVarProjectionFqn: Qn

  protected def reqModelProjectionQn: Qn

  protected def reqModelProjectionParams: String

  protected def metaGenerator(metaOp: OpProjectionType): ReqProjectionGen =
    throw new RuntimeException("meta projections not supported")

  // -----------

  protected lazy val params: CodeChunk =
    ReqProjectionGen.generateParams(op.params(), namespace.toString, "raw.params()")

  protected lazy val metaGeneratorOpt: Option[ReqProjectionGen] = {
    val metaOp: OpProjectionType = op.metaProjection().asInstanceOf[OpProjectionType]
    Option(metaOp).map(metaGenerator)
  }

  protected lazy val meta: CodeChunk = metaGeneratorOpt match {
    case Some(g) => CodeChunk(/*@formatter:off*/sn"""\
  public @Nullable ${g.fullClassName} meta() {
    return raw.metaProjection() == null ? null : new ${g.fullClassName} (raw.metaProjection());
  }
"""/*@formatter:on*/ , Set("org.jetbrains.annotations.Nullable"))
    case None => CodeChunk.empty
  }

  protected def classJavadoc =/*@formatter:off*/sn"""\
/**
 * Request output projection for {@link ${lqn2(cType, namespace.toString)} ${ln(cType)}} type
 */
"""/*@formatter:on*/

}
