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
import ws.epigraph.java.service.projections.req.update.ReqUpdateFieldProjectionGen
import ws.epigraph.java.{GenContext, JavaGen, JavaGenUtils, ObjectGenContext}
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.schema.operations.UpdateOperationDeclaration

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class AbstractUpdateOperationGen(
  val baseNamespace: Qn,
  val rd: ResourceDeclaration,
  val op: UpdateOperationDeclaration,
  val ctx: GenContext) extends AbstractOperationGen {

  protected val updateFieldProjectionGen: ReqUpdateFieldProjectionGen =
    new ReqUpdateFieldProjectionGen(
      new OperationInfoBaseNamespaceProvider(operationInfo),
      rd.fieldName(),
      op.inputProjection(),
      None,
      Qn.EMPTY,
      None,
      ctx
    )

  override def children: Iterable[JavaGen] = super.children ++ Iterable(updateFieldProjectionGen)

  override protected def generate: String = {
    val sctx = new ObjectGenContext(ctx, namespace)

    val outputType = JavaGenUtils.toCType(op.outputType())
    val nsString = namespace.toString
    val resultBuilderCtor = lqbct(outputType, nsString)

    val notnull = sctx.use("org.jetbrains.annotations.NotNull")
    val nullable = sctx.use("org.jetbrains.annotations.Nullable")
    val ureq = sctx.use("ws.epigraph.service.operations.UpdateOperationRequest")
    val rresp = sctx.use("ws.epigraph.service.operations.ReadOperationResponse")
    val cfut = sctx.use("java.util.concurrent.CompletableFuture")
    val uproj = sctx.use(updateFieldProjectionGen.fullClassName)
    val oproj = sctx.use(outputFieldProjectionGen.fullClassName)
    val shortDataType = sctx.use(lqdrn2(outputType, nsString))
    val shortBuilderType = sctx.use(lqbrn(outputType, nsString))

    val inputType = JavaGenUtils.toCType(op.inputType())
    val inputTypeClass = lqn2(inputType, nsString)

    pathProjectionGenOpt match {

      case Some(pathProjectionGen) =>
        val path = sctx.use(pathProjectionGen.fullClassName)
        sctx.addMethod(/*@formatter:off*/sn"""\
@Override
public @$notnull $cfut<$rresp<$shortDataType>> process(@$notnull $ureq request) {
  $shortBuilderType builder = $resultBuilderCtor;
  ${pathProjectionGen.shortClassName} path = new ${pathProjectionGen.shortClassName}(request.path());
  $inputTypeClass data = ${AbstractOperationGen.dataExpr(inputType, nsString, "request.data()")};
  $uproj updateProjection = request.updateProjection() == null ? null : new $uproj(request.updateProjection());
  $oproj outputProjection = new $oproj(request.outputProjection());
  return process(builder, data, path, updateProjection, outputProjection).thenApply($rresp::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process update request
 *
 * @param resultBuilder result builder, initially empty
 * @param updateData update data
 * @param path request path
 * @param updateProjection request update projection, may be {@code null} if not specified
 * @param outputProjection request output projection
 *
 * @return future of the result
 */
protected abstract @$notnull $cfut<$shortDataType> process(
  @$notnull $shortBuilderType resultBuilder,
  @$notnull $inputTypeClass updateData,
  @$notnull $path path,
  @$nullable $uproj updateProjection,
  @$notnull $oproj outputProjection
);
"""/*@formatter:off*/
        )

      case None =>
        sctx.addMethod(/*@formatter:off*/sn"""\
@Override
public @$notnull $cfut<$rresp<$shortDataType>> process(@$notnull $ureq request) {
  $shortBuilderType builder = $resultBuilderCtor;
  $inputTypeClass data = ${AbstractOperationGen.dataExpr(inputType, nsString, "request.data()")};
  $uproj updateProjection = request.updateProjection() == null ? null : new $uproj(request.updateProjection());
  $oproj outputProjection = new $oproj(request.outputProjection());
  return process(builder, data, updateProjection, outputProjection).thenApply($rresp::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process update request
 *
 * @param resultBuilder result builder, initially empty
 * @param updateData update data
 * @param updateProjection request update projection, may be {@code null} if not specified
 * @param outputProjection request output projection
 *
 * @return future of the result
 */
protected abstract @$notnull $cfut<$shortDataType> process(
  @$notnull $shortBuilderType resultBuilder,
  @$notnull $inputTypeClass updateData,
  @$nullable $uproj updateProjection,
  @$notnull $oproj outputProjection
);
"""/*@formatter:off*/
        )
    }

    generate(sctx)
  }
}
