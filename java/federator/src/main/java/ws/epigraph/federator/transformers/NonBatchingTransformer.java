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

package ws.epigraph.federator.transformers;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.data.Data;
import ws.epigraph.invocation.InvocationResult;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.schema.TransformerDeclaration;

import java.util.concurrent.CompletableFuture;

/**
 * Non-batching transformer implementation base class
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class NonBatchingTransformer extends Transformer<Void> {
  protected NonBatchingTransformer(final @NotNull TransformerDeclaration declaration) {
    super(declaration);
  }

  /**
   * Runs transformation
   *
   * @param input            input data
   * @param outputProjection output projection
   *
   * @return output data result future
   */
  public abstract @NotNull CompletableFuture<InvocationResult<Data>> transform(
      @NotNull Data input,
      @NotNull ReqOutputVarProjection outputProjection);

  @Override
  public @NotNull Void newBatch() { return null; }

  @Override
  public @NotNull CompletableFuture<InvocationResult<Data>> transform(
      @NotNull Data input,
      @NotNull ReqOutputVarProjection outputProjection,
      @NotNull Void batch) {
    return transform(input, outputProjection);
  }

  @Override
  public void run(final @NotNull Void batch) { }
}
