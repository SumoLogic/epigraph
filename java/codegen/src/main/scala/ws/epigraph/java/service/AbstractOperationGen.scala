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

package ws.epigraph.java.service

import java.nio.file.Path

import ws.epigraph.compiler.{CDatumType, CType, CVarTypeDef}
import ws.epigraph.java.JavaGenNames.lqdrn2
import ws.epigraph.java.JavaGenUtils.up
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.projections.req.OperationInfo
import ws.epigraph.java.service.projections.req.output.ReqOutputFieldProjectionGen
import ws.epigraph.java.service.projections.req.path.ReqPathFieldProjectionGen
import ws.epigraph.java.{GenContext, JavaGen, JavaGenNames, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.schema.operations.OperationDeclaration

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait AbstractOperationGen extends JavaGen {
  protected def baseNamespace: Qn
  protected def rd: ResourceDeclaration
  protected def op: OperationDeclaration
  protected def ctx: GenContext

  val namespace: Qn = AbstractOperationGen.abstractOperationNamespace(baseNamespace, rd, op)

  val shortClassName: String = AbstractOperationGen.abstractOperationClassName(op)

  override protected def relativeFilePath: Path = JavaGenUtils.fqnToPath(namespace).resolve(shortClassName + ".java")

  protected val operationInfo = OperationInfo(baseNamespace, rd.fieldName(), op)

  protected val pathProjectionGenOpt: Option[ReqPathFieldProjectionGen] =
    Option(op.path()).map { opPath =>
      new ReqPathFieldProjectionGen(
        operationInfo,
        rd.fieldName,
        opPath,
        Qn.EMPTY,
        ctx
      )
    }

  protected val outputFieldProjectionGen = new ReqOutputFieldProjectionGen(
    operationInfo,
    rd.fieldName,
    op.outputProjection,
    None,
    Qn.EMPTY,
    ctx
  )

  override def children: Iterable[JavaGen] = super.children ++
                                             Iterable(outputFieldProjectionGen) ++
                                             pathProjectionGenOpt.toIterable

  protected def generate(sctx: ServiceGenContext): String = {
    val operationKindLower = ServiceNames.operationKinds(op.kind())
    val operationKindUpper = up(operationKindLower)

    val outputType = JavaGenUtils.toCType(op.outputType())
    val nsString = namespace.toString

    sctx.addImport("org.jetbrains.annotations.NotNull")
    sctx.addImport(s"ws.epigraph.service.operations.${ operationKindUpper }Operation")
    sctx.addImport(s"ws.epigraph.service.operations.${ operationKindUpper }OperationRequest")
    sctx.addImport(s"ws.epigraph.service.operations.ReadOperationResponse")  // response is always 'read'
    sctx.addImport(s"ws.epigraph.schema.operations.${ operationKindUpper }OperationDeclaration")
    sctx.addImport("java.util.concurrent.CompletableFuture")
    sctx.addImport(outputFieldProjectionGen.fullClassName)
    val shortDataType = sctx.addImport(lqdrn2(outputType, nsString), namespace)

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ServiceGenUtils.genImports(sctx)}
/**
 * Abstract base class for ${rd.fieldName()} ${Option(op.name()).map(_ + " ").getOrElse("")}$operationKindLower operation
 */
public abstract class $shortClassName extends ${operationKindUpper}Operation<$shortDataType> {

  protected $shortClassName(@NotNull ${operationKindUpper}OperationDeclaration declaration) {
    super(declaration);
  }
  ${i(ServiceGenUtils.genMethods(sctx))}\
  ${i(ServiceGenUtils.genStatic(sctx))}
}
"""/*@formatter:on*/
  }
}

object AbstractOperationGen {
  def abstractOperationNamespace(baseNamespace: Qn, rd: ResourceDeclaration, op: OperationDeclaration): Qn =
    ServiceNames.operationNamespace(baseNamespace, rd.fieldName(), op)

  def abstractOperationClassName(op: OperationDeclaration): String =
    s"Abstract${ up(op.kind().toString.toLowerCase) }${ Option(op.name()).map(up).getOrElse("") }Operation"

  def dataExpr(dataType: CType, namespace: String, rawDataExpr: String): String = {
    val typeExpr = JavaGenNames.lqn2(dataType, namespace)
    dataType match {
      case t: CVarTypeDef => s"($typeExpr) $rawDataExpr" // todo double check
      case t: CDatumType => s"($typeExpr) $rawDataExpr._raw().getDatum($typeExpr.type.self)"
      case other => throw new IllegalArgumentException("Unknown type kind: " + other.getClass.getName)
    }
  }
}
