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
 * Entity transformer implementation base class.
 * <p>
 * Instance lifecycle:
 * <ul>
 * <li>{@link #newBatch} is called to create new batch {@code B}</li>
 * <li>{@link #transform} is called, possibly multiple times with the same batch {@code B}</li>
 * <li>finally {@link #run} is called to execute the batch. {@code B} can't be used after this</li>
 * </ul>
 * <p>
 * Transformer implementation can chose to run underlying actions either immediately in
 * {@code transform} (for instance if batching is not supported), or after {@code run} is called.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class Transformer<B> {
  private final @NotNull TransformerDeclaration declaration;

  protected Transformer(@NotNull TransformerDeclaration declaration) {this.declaration = declaration;}

  public @NotNull TransformerDeclaration declaration() { return declaration; }

  /**
   * Creates new batching context. It can be passed to a series of {@code transform} calls for
   * batch accumulation.
   *
   * @return batching context
   */
  public abstract @NotNull B newBatch();

  /**
   * Runs transformation or schedules it for batched execution
   *
   * @param input            input data
   * @param outputProjection output projection
   * @param batch            batching context
   *
   * @return output data result future
   */
  public abstract @NotNull CompletableFuture<InvocationResult<Data>> transform(
      @NotNull Data input,
      @NotNull ReqOutputVarProjection outputProjection,
      @NotNull B batch
  );

  /**
   * Submits batching context for execution.
   *
   * @param batch batching context
   */
  public abstract void run(@NotNull B batch);

}
