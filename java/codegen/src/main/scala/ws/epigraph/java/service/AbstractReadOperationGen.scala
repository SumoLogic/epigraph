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
import ws.epigraph.java.{GenContext, JavaGenUtils}
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
    val sctx = new ServiceGenContext(ctx)

    val outputType = JavaGenUtils.toCType(op.outputType())
    val nsString = namespace.toString
    val resultBuilderCtor = lqbct(outputType, nsString)

    val shortDataType = sctx.addImport(lqdrn2(outputType, nsString), namespace)
    val shortBuilderType = sctx.addImport(lqbrn(outputType, nsString), namespace)

    pathProjectionGenOpt match {

      case Some(pathProjectionGen) =>
        sctx.addMethod(/*@formatter:off*/sn"""\
  @Override
  public @NotNull CompletableFuture<ReadOperationResponse<$shortDataType>> process(@NotNull final ReadOperationRequest request) {
    $shortBuilderType builder = $resultBuilderCtor;
    ${pathProjectionGen.shortClassName} path = new ${pathProjectionGen.shortClassName}(request.path());
    ${outputFieldProjectionGen.shortClassName} projection = new ${outputFieldProjectionGen.shortClassName}(request.outputProjection());
    return process(builder, path, projection).thenApply(ReadOperationResponse::new);
  }
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
  /**
   * Process read request
   *
   * @param builder result builder, initially empty
   * @param path request path
   * @param projection request projection
   *
   * @return future of the result
   */
  protected abstract @NotNull CompletableFuture<$shortDataType> process($shortBuilderType builder, ${pathProjectionGen.shortClassName} path, ${outputFieldProjectionGen.shortClassName} projection);
"""/*@formatter:off*/
        )

      case None =>
        sctx.addMethod(/*@formatter:off*/sn"""\
  @Override
  public @NotNull CompletableFuture<ReadOperationResponse<$shortDataType>> process(@NotNull final ReadOperationRequest request) {
    $shortBuilderType builder = $resultBuilderCtor;
    ${outputFieldProjectionGen.shortClassName} projection = new ${outputFieldProjectionGen.shortClassName}(request.outputProjection());
    return process(builder, projection).thenApply(ReadOperationResponse::new);
  }
"""/*@formatter:off*/
        )

        sctx.addMethod(/*@formatter:off*/sn"""\
  /**
   * Process read request
   *
   * @param builder result builder, initially empty
   * @param projection request projection
   *
   * @return future of the result
   */
  protected abstract @NotNull CompletableFuture<$shortDataType> process($shortBuilderType builder, ${outputFieldProjectionGen.shortClassName} projection);
"""/*@formatter:off*/
        )
    }

    generate(sctx)
  }
}
