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

import ws.epigraph.java.JavaGenNames.{lqbct, lqbrn, lqdrn2}
import ws.epigraph.java.JavaGenUtils.up
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.projections.req.output.ReqOutputFieldProjectionGen
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.schema.operations.ReadOperationDeclaration

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class AbstractReadOperationGen(
  val baseNamespace: Qn,
  rd: ResourceDeclaration,
  op: ReadOperationDeclaration,
  outputFieldProjectionGen: ReqOutputFieldProjectionGen,
  val ctx: GenContext) extends JavaGen {

  protected val namespace: Qn = AbstractReadOperationGen.abstractReadOperationNamespace(baseNamespace, rd, op)

  override protected def relativeFilePath: Path =
    JavaGenUtils.fqnToPath(namespace).resolve(AbstractReadOperationGen.abstractReadOperationClassName(op) + ".java")

  override protected def generate: String = {
    val sctx = new ServiceGenContext(ctx)
    val className = AbstractReadOperationGen.abstractReadOperationClassName(op)

    val outputType = JavaGenUtils.toCType(op.outputType())
    val nsString = namespace.toString
    val resultBuilderCtor = lqbct(outputType, nsString)

    sctx.addImport("org.jetbrains.annotations.NotNull")
    sctx.addImport("ws.epigraph.service.operations.ReadOperation")
    sctx.addImport("ws.epigraph.service.operations.ReadOperationRequest")
    sctx.addImport("ws.epigraph.service.operations.ReadOperationResponse")
    sctx.addImport("ws.epigraph.schema.operations.ReadOperationDeclaration")
    sctx.addImport("java.util.concurrent.CompletableFuture")
    sctx.addImport(outputFieldProjectionGen.fullClassName)
    val shortDataType = sctx.addImport(lqdrn2(outputType, nsString), namespace)
    val shortBuilderType = sctx.addImport(lqbrn(outputType, nsString), namespace)

    // todo take path into account


    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ServiceGenUtils.genImports(sctx)}
/**
 * Abstract base class for ${rd.fieldName()} ${Option(op.name()).map(_ + " ").getOrElse("")}read operation
 */
public abstract class $className extends ReadOperation<$shortDataType> {
  ${i(ServiceGenUtils.genFields(sctx))}

  protected $className(@NotNull ReadOperationDeclaration declaration) {
    super(declaration);
  }

  @Override
  public @NotNull CompletableFuture<ReadOperationResponse<$shortDataType>> process(@NotNull final ReadOperationRequest request) {
    $shortBuilderType builder = $resultBuilderCtor;
    ${outputFieldProjectionGen.shortClassName} projection = new ${outputFieldProjectionGen.shortClassName}(request.outputProjection());
    return process(builder, projection).thenApply(ReadOperationResponse::new);
  }

  protected abstract @NotNull CompletableFuture<$shortDataType> process($shortBuilderType builder, ${outputFieldProjectionGen.shortClassName} projection);

  ${i(ServiceGenUtils.genMethods(sctx))}
}
"""/*@formatter:on*/
  }
}

object AbstractReadOperationGen {
  // todo move to AbstractOperationGen

  def abstractReadOperationNamespace(baseNamespace: Qn, rd: ResourceDeclaration, op: ReadOperationDeclaration): Qn =
    ServiceNames.operationNamespace(baseNamespace, rd.fieldName(), op)

  def abstractReadOperationClassName(op: ReadOperationDeclaration): String =
    s"Abstract${up(op.kind().toString.toLowerCase)}${Option(op.name()).map(up).getOrElse("")}Operation"
}
