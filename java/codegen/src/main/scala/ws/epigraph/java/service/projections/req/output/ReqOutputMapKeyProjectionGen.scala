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

import ws.epigraph.compiler.{CDatumType, CMapType, CTypeKind}
import ws.epigraph.java.JavaGenNames.{ln, lqn2}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.output.ReqOutputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen}
import ws.epigraph.java.{GenContext, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.output.OpOutputKeyProjection

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqOutputMapKeyProjectionGen(
  operationInfo: OperationInfo,
  mapType: CMapType,
  op: OpOutputKeyProjection,
  namespaceSuffix: Qn,
  ctx: GenContext) extends ReqOutputProjectionGen(operationInfo, namespaceSuffix, ctx) {

  private val mapTypeShortName = ln(mapType)

  override def shortClassName: String = s"$classNamePrefix${mapTypeShortName}Key$classNameSuffix"

  override protected def generate: String = {
    val params = ReqProjectionGen.generateParams(op.params(), namespace.toString, "raw.params()")

    val keyType: CDatumType = mapType.keyTypeRef.resolved.asInstanceOf[CDatumType]
    val keyTypeShortName = ln(keyType)

    def genPrimitiveKey(nativeType: String): String = /*@formatter:off*/sn"""\
  public @NotNull $nativeType value() {
    $keyTypeShortName key = ($keyTypeShortName) raw.value();
    return key.getVal();
  }
"""/*@formatter:on*/

    def genNonPrimitiveKey: String = /*@formatter:off*/sn"""\
  public @NotNull $keyTypeShortName value() {
    return ($keyTypeShortName) raw.value();
  }
"""/*@formatter:on*/

    val keyCode = keyType.kind match {
      case CTypeKind.STRING => genPrimitiveKey("String")
      case CTypeKind.INTEGER => genPrimitiveKey("Integer")
      case CTypeKind.LONG => genPrimitiveKey("Long")
      case CTypeKind.DOUBLE => genPrimitiveKey("Double")
      case CTypeKind.BOOLEAN => genPrimitiveKey("Boolean")
      case _ => genNonPrimitiveKey
    }

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "ws.epigraph.projections.req.output.ReqOutputKeyProjection",
      lqn2(keyType, namespace.toString)
    ) ++ params.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

/**
 * Request output projection for {@code $mapTypeShortName} keys
 */
public class $shortClassName {
  private final @NotNull ReqOutputKeyProjection raw;

  public $shortClassName(@NotNull ReqOutputKeyProjection raw) {
    this.raw = raw;
  }

  /**
   * @return key value
   */
$keyCode\
${params.code}\

  public @NotNull ReqOutputKeyProjection _raw() { return raw; }
}"""/*@formatter:on*/
  }
}
