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
import ws.epigraph.java.service.projections.req.OperationInfoBaseNamespaceProvider
import ws.epigraph.java.service.projections.req.input.ReqInputFieldProjectionGen
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils, ObjectGenContext}
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
      new OperationInfoBaseNamespaceProvider(operationInfo),
      rd.fieldName(),
      op.inputProjection(),
      None,
      Qn.EMPTY,
      None,
      ctx
    )

  override def children: Iterable[JavaGen] = super.children ++ Iterable(inputFieldProjectionGen)

  override protected def generate: String = {
    val nsString = namespace.toString
    val sctx = new ObjectGenContext(ctx, namespace)

    val outputType = JavaGenUtils.toCType(op.outputType())
    val resultBuilderCtor = lqbct(outputType, nsString)

    val creq = sctx.use("ws.epigraph.service.operations.CreateOperationRequest")
    val rresp = sctx.use("ws.epigraph.service.operations.ReadOperationResponse")
    val cfut = sctx.use("java.util.concurrent.CompletableFuture")
    val nullable = sctx.use("org.jetbrains.annotations.Nullable")
    val notnull = sctx.use("org.jetbrains.annotations.NotNull")
    val inputShortName = sctx.use(inputFieldProjectionGen.fullClassName)
    val outputShortName = sctx.use(outputFieldProjectionGen.fullClassName)
    val shortDataType = sctx.use(lqdrn2(outputType, nsString))
    val shortBuilderType = sctx.use(lqbrn(outputType, nsString))

    val inputType = JavaGenUtils.toCType(op.inputType())
    val inputTypeClass = lqn2(inputType, nsString)

    pathProjectionGenOpt match {

      case Some(pathProjectionGen) =>
        val pathShortName = sctx.use(pathProjectionGen.fullClassName)
        sctx.addMethod(/*@formatter:off*/sn"""\
@Override
public @$notnull $cfut<$rresp<$shortDataType>> process(@$notnull $creq request) {
  $shortBuilderType builder = $resultBuilderCtor;
  $pathShortName path = new $pathShortName(request.path());
  $inputTypeClass data = ${AbstractOperationGen.dataExpr(inputType, nsString, "request.data()")};
  $inputShortName inputProjection = request.inputProjection() == null ? null : new $inputShortName(request.inputProjection());
  $outputShortName outputProjection = new $outputShortName(request.outputProjection());
  return process(builder, data, path, inputProjection, outputProjection).thenApply($rresp::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process create request
 *
 * @param resultBuilder result builder, initially empty
 * @param inputData input data
 * @param path request path
 * @param inputProjection request input projection, may be {@code null} if not specified
 * @param outputProjection request output projection
 *
 * @return future of the result
 */
protected abstract @$notnull $cfut<$shortDataType> process(
  @$notnull $shortBuilderType resultBuilder,
  @$notnull $inputTypeClass inputData,
  @$notnull $pathShortName path,
  @$nullable $inputShortName inputProjection,
  @$notnull $outputShortName outputProjection
);
"""/*@formatter:off*/
        )

      case None =>
        sctx.addMethod(/*@formatter:off*/sn"""\
@Override
public @$notnull $cfut<$rresp<$shortDataType>> process(@$notnull $creq request) {
  $shortBuilderType builder = $resultBuilderCtor;
  $inputTypeClass data = ${AbstractOperationGen.dataExpr(inputType, nsString, "request.data()")};
  $inputShortName inputProjection = request.inputProjection() == null ? null : new $inputShortName(request.inputProjection());
  $outputShortName outputProjection = new $outputShortName(request.outputProjection());
  return process(builder, data, inputProjection, outputProjection).thenApply($rresp::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process create request
 *
 * @param resultBuilder result builder, initially empty
 * @param inputData input data
 * @param inputProjection request input projection, may be {@code null} if not specified
 * @param outputProjection request output projection
 *
 * @return future of the result
 */
protected abstract @$notnull $cfut<$shortDataType> process(
  @$notnull $shortBuilderType resultBuilder,
  @$notnull $inputTypeClass inputData,
  @$nullable $inputShortName inputProjection,
  @$notnull $outputShortName outputProjection
);
"""/*@formatter:off*/
        )
    }

    generate(sctx)
  }
}
