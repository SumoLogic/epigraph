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

import ws.epigraph.compiler.CType
import ws.epigraph.java.JavaGenNames.{jn, ln}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen}
import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output._
import ws.epigraph.types.TypeKind

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputVarProjectionGen(
  operationInfo: OperationInfo,
  op: OpOutputVarProjection,
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputProjectionGen(operationInfo, namespaceSuffix, ctx) {

  // todo we have to deal with poly tails / normalization in generated classes

  private val cType: CType = ReqProjectionGen.toCType(op.`type`())

  override protected val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  override def children: Iterable[ReqProjectionGen] = op.tagProjections().values().map{ tpe =>
    ReqOutputModelProjectionGen.dataProjectionGen(
      operationInfo,
      tpe.projection(),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase), // todo extract?
      ctx
    )
  }

  override protected def generate: String =
  /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

import org.jetbrains.annotations.NotNull;

import ws.epigraph.projections.req.output.ReqOutputPrimitiveModelProjection;

/**
 * Request output projection for {@code ${ln(cType)}} type
 */
public class $shortClassName {
  private final @NotNull ReqOutputPrimitiveModelProjection raw;

  public $shortClassName(@NotNull ReqOutputPrimitiveModelProjection raw) { this.raw = raw; }

  public @NotNull ReqOutputPrimitiveModelProjection _raw() { return raw; }
}"""/*@formatter:on*/
}

object ReqOutputVarProjectionGen {
  def dataProjectionGen(
    operationInfo: OperationInfo,
    op: OpOutputVarProjection,
    namespaceSuffix: Qn,
    ctx: GenContext): ReqOutputProjectionGen = op.`type`().kind() match {

    case TypeKind.UNION =>
      new ReqOutputVarProjectionGen(operationInfo, op, namespaceSuffix, ctx)
    case TypeKind.RECORD =>
      new ReqOutputRecordModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputRecordModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqOutputMapModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputMapModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.LIST =>
      new ReqOutputListModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputListModelProjection],
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqOutputPrimitiveModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpOutputPrimitiveModelProjection],
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unknown projection kind: $x")

  }
}
