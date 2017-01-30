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

package ws.epigraph.java.projections.req.output

import ws.epigraph.compiler._
import ws.epigraph.projections.req.output._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
object ReqOutputProjectionGenUtil {

  /**
   * @param t  projection type
   * @param ln projection type local (short) java class name
   *
   * @return projection expression
   */
  def projectionExpr(t: CType, ln: String): ProjectionExpr = {
    val kind = t.kind

    if (kind.isPrimitive) {
      ProjectionExpr(
        "ReqOutputPrimitiveModelProjection",
        r => s"(ReqOutputPrimitiveModelProjection) $r.singleTagProjection().projection()",
        r => s"(ReqOutputPrimitiveModelProjection) $r",
        Set(classOf[ReqOutputPrimitiveModelProjection].getName)
      )
    } else kind match {
      case CTypeKind.VARTYPE =>
        val pc = ReqOutputVarProjectionGen.shortClassName(t.asInstanceOf[CVarTypeDef])
        ProjectionExpr(
          pc,
          r => s"new $pc($r)",
          _ => throw new RuntimeException(s"Unexpected kind: $kind of ${t.name.name}"), //s"new $pc($r)"
          Set(classOf[ReqOutputVarProjection].getName)
        )
      case CTypeKind.RECORD =>
        val pc = ReqOutputRecordModelProjectionGen.shortClassName(t.asInstanceOf[CRecordTypeDef])
        ProjectionExpr(
          pc,
          r => s"new $pc((ReqOutputRecordModelProjection) $r.singleTagProjection().projection())",
          r => s"new $pc((ReqOutputRecordModelProjection) $r)",
          Set(classOf[ReqOutputRecordModelProjection].getName)
        )
      case CTypeKind.MAP =>
        val pc = ReqOutputMapModelProjectionGen.shortClassName(t.asInstanceOf[CMapType])
        ProjectionExpr(
          pc,
          r => s"new $pc((ReqOutputMapModelProjection) $r.singleTagProjection().projection())",
          r => s"new $pc((ReqOutputMapModelProjection) $r)",
          Set(classOf[ReqOutputMapModelProjection].getName)
        )
      case CTypeKind.LIST =>
        val pc = ReqOutputListModelProjectionGen.shortClassName(t.asInstanceOf[CListType])
        ProjectionExpr(
          pc,
          r => s"new $pc((ReqOutputListModelProjection) $r.singleTagProjection().projection())",
          r => s"new $pc((ReqOutputListModelProjection) $r)",
          Set(classOf[ReqOutputListModelProjection].getName)
        )
      case _ => throw new RuntimeException("Unexpected model kind: " + t)
    }
  }

  /**
   * Projection expression descriptor
   *
   * @param resultType           (generated) projection class name, expression result type
   * @param fromVarExprBuilder   raw var projection expression to generated projection expression function
   * @param fromModelExprBuilder raw model projection expression to generated projection expression function
   * @param extraImports         extra imports to add
   */
  sealed case class ProjectionExpr(
    resultType: String,
    fromVarExprBuilder: String => String,
    fromModelExprBuilder: String => String,
    extraImports: Set[String] = Set()
  )

}
