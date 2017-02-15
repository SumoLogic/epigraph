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

import ws.epigraph.java.JavaGenNames.{lqbct, lqbrn, lqdrn2, lqn2}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.input.ReqInputFieldProjectionGen
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils}
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.schema.operations.CreateOperationDeclaration

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class AbstractCreateOperationGen(
  val baseNamespace: Qn,
  val rd: ResourceDeclaration,
  val op: CreateOperationDeclaration,
  val ctx: GenContext) extends AbstractOperationGen {

  protected val inputFieldProjectionGen: ReqInputFieldProjectionGen =
    new ReqInputFieldProjectionGen(
      operationInfo,
      rd.fieldName(),
      op.inputProjection(),
      Qn.EMPTY,
      ctx
    )

  override def children: Iterable[JavaGen] = super.children ++ Iterable(inputFieldProjectionGen)

  override protected def generate: String = {
    val sctx = new ServiceGenContext(ctx)

    val outputType = JavaGenUtils.toCType(op.outputType())
    val nsString = namespace.toString
    val resultBuilderCtor = lqbct(outputType, nsString)

    sctx.addImport("org.jetbrains.annotations.Nullable")
    sctx.addImport(inputFieldProjectionGen.fullClassName)
    val shortDataType = sctx.addImport(lqdrn2(outputType, nsString), namespace)
    val shortBuilderType = sctx.addImport(lqbrn(outputType, nsString), namespace)

    val inputType = JavaGenUtils.toCType(op.inputType())
    val inputTypeClass = lqn2(inputType, nsString)

    pathProjectionGenOpt match {

      case Some(pathProjectionGen) =>
        sctx.addMethod(/*@formatter:off*/sn"""\
@Override
public @NotNull CompletableFuture<ReadOperationResponse<$shortDataType>> process(@NotNull CreateOperationRequest request) {
  $shortBuilderType builder = $resultBuilderCtor;
  ${pathProjectionGen.shortClassName} path = new ${pathProjectionGen.shortClassName}(request.path());
  $inputTypeClass data = ${AbstractOperationGen.dataExpr(inputType, nsString, "request.data()")};
  ${inputFieldProjectionGen.shortClassName} inputProjection = request.inputProjection() == null ? null : new ${inputFieldProjectionGen.shortClassName}(request.inputProjection());
  ${outputFieldProjectionGen.shortClassName} outputProjection = new ${outputFieldProjectionGen.shortClassName}(request.outputProjection());
  return process(builder, data, path, inputProjection, outputProjection).thenApply(ReadOperationResponse::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process create request
 *
 * @param builder result builder, initially empty
 * @param inputData input data
 * @param path request path
 * @param inputProjection request input projection, may be {@code null} if not specified
 * @param outputProjection request output projection
 *
 * @return future of the result
 */
protected abstract @NotNull CompletableFuture<$shortDataType> process(
  @NotNull $shortBuilderType builder,
  @NotNull $inputTypeClass inputData,
  @NotNull ${pathProjectionGen.shortClassName} path,
  @Nullable ${inputFieldProjectionGen.shortClassName} inputProjection,
  @NotNull ${outputFieldProjectionGen.shortClassName} outputProjection
);
"""/*@formatter:off*/
        )

      case None =>
        sctx.addMethod(/*@formatter:off*/sn"""\
@Override
public @NotNull CompletableFuture<ReadOperationResponse<$shortDataType>> process(@NotNull CreateOperationRequest request) {
  $shortBuilderType builder = $resultBuilderCtor;
  $inputTypeClass data = ${AbstractOperationGen.dataExpr(inputType, nsString, "request.data()")};
  ${inputFieldProjectionGen.shortClassName} inputProjection = request.inputProjection() == null ? null : new ${inputFieldProjectionGen.shortClassName}(request.inputProjection());
  ${outputFieldProjectionGen.shortClassName} outputProjection = new ${outputFieldProjectionGen.shortClassName}(request.outputProjection());
  return process(builder, data, inputProjection, outputProjection).thenApply(ReadOperationResponse::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process create request
 *
 * @param builder result builder, initially empty
 * @param inputData input data
 * @param inputProjection request input projection, may be {@code null} if not specified
 * @param outputProjection request output projection
 *
 * @return future of the result
 */
protected abstract @NotNull CompletableFuture<$shortDataType> process(
  @NotNull $shortBuilderType builder,
  @NotNull $inputTypeClass inputData,
  @Nullable ${inputFieldProjectionGen.shortClassName} inputProjection,
  @NotNull ${outputFieldProjectionGen.shortClassName} outputProjection
);
"""/*@formatter:off*/
        )
    }

    generate(sctx)
  }
}
