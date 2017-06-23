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

import ws.epigraph.java.JavaGenNames.{lqbct, lqbrn, lqdrn2}
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.{GenContext, JavaGenUtils, ObjectGenContext}
import ws.epigraph.lang.Qn
import ws.epigraph.schema.ResourceDeclaration
import ws.epigraph.schema.operations.ReadOperationDeclaration

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class AbstractReadOperationGen(
  val baseNamespace: Qn,
  val rd: ResourceDeclaration,
  val op: ReadOperationDeclaration,
  val ctx: GenContext) extends AbstractOperationGen {

  override protected def generate: String = {
    val sctx = new ObjectGenContext(ctx, namespace)

    val outputType = JavaGenUtils.toCType(op.outputType())
    val nsString = namespace.toString
    val resultBuilderCtor = lqbct(outputType, nsString)

    val rreq = sctx.use("ws.epigraph.service.operations.ReadOperationRequest")
    val rresp = sctx.use("ws.epigraph.service.operations.ReadOperationResponse")
    val cfut = sctx.use("java.util.concurrent.CompletableFuture")
    val notnull = sctx.use("org.jetbrains.annotations.NotNull")
    val outputShortName = sctx.use(outputFieldProjectionGen.fullClassName)
    val shortDataType = sctx.use(lqdrn2(outputType, nsString))
    val shortBuilderType = sctx.use(lqbrn(outputType, nsString))

    pathProjectionGenOpt match {

      case Some(pathProjectionGen) =>
        val pathShortName = sctx.use(pathProjectionGen.fullClassName)
        sctx.addMethod(/*@formatter:off*/sn"""\
@Override
public @$notnull $cfut<$rresp<$shortDataType>> process(@$notnull $rreq request) {
  $shortBuilderType builder = $resultBuilderCtor;
  $pathShortName path = new $pathShortName(request.path());
  $outputShortName projection = new $outputShortName(request.outputProjection());
  return process(builder, path, projection).thenApply($rresp::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process read request
 *
 * @param resultBuilder result builder, initially empty
 * @param path request path
 * @param projection request projection
 *
 * @return future of the result
 */
protected abstract @$notnull $cfut<$shortDataType> process(@$notnull $shortBuilderType resultBuilder, @$notnull $pathShortName path, @$notnull $outputShortName projection);
"""/*@formatter:off*/
        )

      case None =>
        sctx.addMethod(/*@formatter:off*/sn"""\
@Override
public @$notnull $cfut<$rresp<$shortDataType>> process(@$notnull final $rreq request) {
  $shortBuilderType builder = $resultBuilderCtor;
  $outputShortName projection = new $outputShortName(request.outputProjection());
  return process(builder, projection).thenApply($rresp::new);
}
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
/**
 * Process read request
 *
 * @param resultBuilder result builder, initially empty
 * @param projection request projection
 *
 * @return future of the result
 */
protected abstract @$notnull $cfut<$shortDataType> process(@$notnull $shortBuilderType resultBuilder, @$notnull $outputShortName projection);
"""/*@formatter:off*/
        )
    }

    generate(sctx)
  }
}
