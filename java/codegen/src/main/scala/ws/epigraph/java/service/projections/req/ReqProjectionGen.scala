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

import ws.epigraph.compiler._
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.ProjectionGenUtil
import ws.epigraph.java.{JavaGen, JavaGenNames, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.ProjectionReferenceName
import ws.epigraph.projections.op.OpParams
import ws.epigraph.types.DatumTypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqProjectionGen extends JavaGen {
  protected type GenType <: ReqProjectionGen

  protected val baseNamespaceProvider: BaseNamespaceProvider

  protected def baseNamespace: Qn = baseNamespaceProvider.baseNamespace

  protected def namespaceSuffix: Qn = Qn.EMPTY

  final def namespace: Qn = {
    val r = baseNamespace.append(namespaceSuffix)
//    System.out.println(s"namespace for $shortClassName = $r")
    r
  }

  def shortClassName: String

  def fullClassName: String = namespace.append(shortClassName).toString

  protected def parentClassGenOpt: Option[GenType] = None

//  override def children: Iterable[JavaGen] = parentClassGenOpt

  override def relativeFilePath: Path = JavaGenUtils.fqnToPath(namespace).resolve(shortClassName + ".java")

  protected val packageStatement: String = s"package $namespace;"

  override def description: String = s"${super.description} $namespace::$shortClassName"

  def extendsClause: String = parentClassGenOpt.map(p => s"extends ${ p.fullClassName } ").getOrElse("")
}

object ReqProjectionGen {
  val classNamePrefix: String = "" // "Req" // we don't generate "Op", so this should be OK ?
  val classNameSuffix: String = "Projection"

  def baseNamespace(referenceName: Option[ProjectionReferenceName], default: Qn): Qn =
    referenceName.map(n => JavaGenNames.pnq(ProjectionGenUtil.toQn(n))).getOrElse(default)

  def namespaceSuffix(name: Option[ProjectionReferenceName], default: Qn): Qn = {
    val r = name.map(_ => Qn.EMPTY).getOrElse(default)
//    System.out.println(s"namespaceSuffix($name, $default)->'$r'")
    r
  }

  def generateParams(op: OpParams, namespace: String, reqParamsExpr: String): CodeChunk = {
    import scala.collection.JavaConversions._

    op.asMap().values().map { p =>

      val datumType: CDatumType = JavaGenUtils.toCType(p.projection().`type`().asInstanceOf[DatumTypeApi])
      // Scala doesn't get it
      val valueType = JavaGenNames.lqn2(datumType, namespace)

      val notnull = p.projection().flagged() || p.projection().defaultValue() != null
      val nullAnnotation = if (notnull) "@NotNull" else "@Nullable"
      val nullHandlingCode = if (notnull) "assert param != null;" else "if (param == null) return null;"

      def genPrimitiveParam(nativeType: String): String = /*@formatter:off*/sn"""\
  /**
   * @return {@code ${p.name()}} parameter value
   */
  public $nullAnnotation $nativeType get${JavaGenUtils.up(p.name())}Parameter() {
    ReqParam param = $reqParamsExpr.get("${p.name()}");
    $nullHandlingCode
    $valueType nativeValue = ($valueType) param.value();
    return nativeValue == null ? null : nativeValue.getVal();
  }
"""/*@formatter:on*/

      def genNonPrimitiveParam: String = /*@formatter:off*/sn"""\
  /**
   * @return {@code ${p.name()}} parameter value
   */
  public $nullAnnotation $valueType get${JavaGenUtils.up(p.name())}Parameter() {
    ReqParam param = $reqParamsExpr.get("${p.name()}");
    $nullHandlingCode
    return ($valueType) param.value();
  }
"""/*@formatter:on*/

      // unwrap primitive param accessors to return native values
      val paramCode = datumType.kind match {
        case CTypeKind.STRING => genPrimitiveParam("String")
        case CTypeKind.INTEGER => genPrimitiveParam("Integer")
        case CTypeKind.LONG => genPrimitiveParam("Long")
        case CTypeKind.DOUBLE => genPrimitiveParam("Double")
        case CTypeKind.BOOLEAN => genPrimitiveParam("Boolean")
        case _ => genNonPrimitiveParam
      }

      CodeChunk(
        paramCode, Set(
          "org.jetbrains.annotations.Nullable",
          "org.jetbrains.annotations.NotNull",
          "ws.epigraph.projections.req.ReqParam"
        )
      )
    }.foldLeft(CodeChunk.empty)(_ + _)
  }

}
