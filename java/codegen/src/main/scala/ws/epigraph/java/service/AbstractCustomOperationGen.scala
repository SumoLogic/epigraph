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
import ws.epigraph.java.service.projections.req.{OperationInfoBaseNamespaceProvider, ReqFieldProjectionGen}
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils, ObjectGenContext}
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.schema.operations.CustomOperationDeclaration

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class AbstractCustomOperationGen(
  val baseNamespace: Qn,
  val rd: ResourceDeclaration,
  val op: CustomOperationDeclaration,
  val ctx: GenContext) extends AbstractOperationGen {

  protected val inputFieldProjectionGenOpt: Option[ReqFieldProjectionGen] =
    Option(op.inputProjection()).map { inputProjection =>
      new ReqInputFieldProjectionGen(
        new OperationInfoBaseNamespaceProvider(operationInfo),
        rd.fieldName(),
        inputProjection,
        None,
        Qn.EMPTY,
        None,
        ctx
      )
    }

  override def children: Iterable[JavaGen] = super.children ++ inputFieldProjectionGenOpt.toIterable

  override protected def generate: String = {
    val nsString = namespace.toString
    val sctx = new ObjectGenContext(ctx, namespace)

    val outputType = JavaGenUtils.toCType(op.outputType())
    val resultBuilderCtor = lqbct(outputType, nsString)

    val creq = sctx.use("ws.epigraph.service.operations.CustomOperationRequest")
    val rresp = sctx.use("ws.epigraph.service.operations.ReadOperationResponse")
    val cfut = sctx.use("java.util.concurrent.CompletableFuture")
    val nullable = sctx.use("org.jetbrains.annotations.Nullable")
    val notnull = sctx.use("org.jetbrains.annotations.NotNull")
    val shortDataType = sctx.use(lqdrn2(outputType, nsString))
    val shortBuilderType = sctx.use(lqbrn(outputType, nsString))

    inputFieldProjectionGenOpt match {
      case Some(inputFieldProjectionGen) =>

        val inputShortName = sctx.use(inputFieldProjectionGen.fullClassName)
        val outputShortName = sctx.use(outputFieldProjectionGen.fullClassName)

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
  $inputTypeClass data = request.data() == null ? null : ${AbstractOperationGen.dataExpr(inputType, nsString, "request.data()")};
  $inputShortName inputProjection = request.inputProjection() == null ? null : new $inputShortName(request.inputProjection());
  $outputShortName outputProjection = new $outputShortName(request.outputProjection());
  return process(builder, data, path, inputProjection, outputProjection).thenApply($rresp::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process custom request
 *
 * @param resultBuilder result builder, initially empty
 * @param inputData input data, may be {@code null} if not specified
 * @param path request path
 * @param inputProjection request input projection, may be {@code null} if not specified
 * @param outputProjection request output projection
 *
 * @return future of the result
 */
protected abstract @$notnull $cfut<$shortDataType> process(
  @$notnull $shortBuilderType resultBuilder,
  @$nullable $inputTypeClass inputData,
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
  $inputTypeClass data = request.data() == null ? null : ${AbstractOperationGen.dataExpr(inputType, nsString, "request.data()")};
  $inputShortName inputProjection = request.inputProjection() == null ? null : new $inputShortName(request.inputProjection());
  $outputShortName outputProjection = new $outputShortName(request.outputProjection());
  return process(builder, data, inputProjection, outputProjection).thenApply($rresp::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process custom request
 *
 * @param resultBuilder result builder, initially empty
 * @param inputData input data, may be {@code null} if not specified
 * @param inputProjection request input projection, may be {@code null} if not specified
 * @param outputProjection request output projection
 *
 * @return future of the result
 */
protected abstract @$notnull $cfut<$shortDataType> process(
  @$notnull $shortBuilderType resultBuilder,
  @$nullable $inputTypeClass inputData,
  @$nullable $inputShortName inputProjection,
  @$notnull $outputShortName outputProjection
);
"""/*@formatter:off*/
        )
    }

        // case when input projection and data are not supported =================================================

      case None =>
        pathProjectionGenOpt match {

          case Some(pathProjectionGen) =>
            val outputShortName = sctx.use(outputFieldProjectionGen.fullClassName)
            val pathShortName = sctx.use(pathProjectionGen.fullClassName)
            sctx.addMethod(/*@formatter:off*/sn"""\
@Override
public @$notnull $cfut<$rresp<$shortDataType>> process(@$notnull $creq request) {
  $shortBuilderType builder = $resultBuilderCtor;
  $pathShortName path = new ${pathProjectionGen.shortClassName}(request.path());
  $outputShortName outputProjection = new $outputShortName(request.outputProjection());
  return process(builder, path, inputProjection, outputProjection).thenApply($rresp::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process custom request
 *
 * @param resultBuilder result builder, initially empty
 * @param path request path
 * @param outputProjection request output projection
 *
 * @return future of the result
 */
protected abstract @$notnull $cfut<$shortDataType> process(
  @$notnull $shortBuilderType resultBuilder,
  @$notnull $pathShortName path,
  @$notnull $outputShortName outputProjection
);
"""/*@formatter:off*/
        )

      case None =>
        val outputShortName = sctx.use(outputFieldProjectionGen.fullClassName)
        sctx.addMethod(/*@formatter:off*/sn"""\
@Override
public @$notnull $cfut<$rresp<$shortDataType>> process(@$notnull $creq request) {
  $shortBuilderType builder = $resultBuilderCtor;
  $outputShortName outputProjection = new $outputShortName(request.outputProjection());
  return process(builder, inputProjection, outputProjection).thenApply($rresp::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process custom request
 *
 * @param resultBuilder result builder, initially empty
 * @param outputProjection request output projection
 *
 * @return future of the result
 */
protected abstract @$notnull $cfut<$shortDataType> process(@$notnull $shortBuilderType resultBuilder, @$notnull $outputShortName outputProjection);
"""/*@formatter:off*/
        )
    }

    }


    generate(sctx)
  }
}
