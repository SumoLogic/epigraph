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

import java.nio.file.Path

import ws.epigraph.compiler.{CDatumType, CDatumTypeApiWrapper, CType, CTypeApiWrapper}
import ws.epigraph.java.{GenContext, JavaGen, JavaGenNames, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.OpParams
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.types.{DatumTypeApi, TypeApi}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ReqProjectionGen(protected val operationInfo: OperationInfo, ctx: GenContext) extends JavaGen(ctx) {

  protected def namespace: Qn =
    operationInfo.resourceNamespace
      .append("resources")
      .append(operationInfo.resourceName.toLowerCase)
      .append("operations")
      .append(s"${operationInfo.operation.kind()}${Option(operationInfo.operation.name()).getOrElse("")}".toLowerCase)

  protected def shortClassName: String

  override protected def relativeFilePath: Path = JavaGenUtils.fqnToPath(namespace).resolve(shortClassName + ".java")

  def children: Iterable[ReqProjectionGen] = Iterable()

  protected val packageStatement: String = s"package $namespace;"
}

object ReqProjectionGen {
  val classNamePrefix: String = "Req"
  val classNameSuffix: String = "Projection"

  def generateParams(op: OpParams, namespace: String, reqParamsExpr: String): (String, Set[String]) = {
    import scala.collection.JavaConversions._

    op.asMap().values().foldLeft(("", Set[String]())){ case ((code, imports), p) =>

      val m: DatumTypeApi = p.projection().model().asInstanceOf[DatumTypeApi]
      // Scala doesn't get it
      val valueType = JavaGenNames.lqn2(toCType(m), namespace)

      val paramCode =
      /*@formatter:off*/sn"""\
  /**
   * @return {@code ${p.name()}} parameter value
   */
  public @Nullable $valueType get${JavaGenUtils.up(p.name())}Parameter() {
    ReqParam param = $reqParamsExpr.get("${p.name()}");
    return param == null ? null : ($valueType) param.value();
  }
"""/*@formatter:on*/

      val newCode = if (code.isEmpty) "\n" + paramCode else code + "\n" + paramCode
      (newCode, imports ++ Set(
        "org.jetbrains.annotations.Nullable",
        "ws.epigraph.projections.req.ReqParam",
        valueType
      ))
    }
  }

  def generateImports(imports: Set[String]): String = imports.toList.sorted.mkString("import ", ";\nimport ", ";")

  def toCType(t: TypeApi): CType = t.asInstanceOf[CTypeApiWrapper].cType

  def toCType(t: DatumTypeApi): CDatumType = t.asInstanceOf[CDatumTypeApiWrapper].cType
}
