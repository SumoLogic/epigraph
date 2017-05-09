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

package ws.epigraph.client.http;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.invocation.OperationInvocation;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationResult;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RemoteReadOperationInvocation
    implements OperationInvocation<ReadOperationRequest, ReadOperationResponse<?>> {

  private final @NotNull HttpRequestDispatcher serverConnection;
  private final @NotNull String resourceName;
//  private final @NotNull ServerProtocol serverProtocol;

  public RemoteReadOperationInvocation(
      final @NotNull HttpRequestDispatcher serverConnection,
      final @NotNull String resourceName) {

    this.serverConnection = serverConnection;
    this.resourceName = resourceName;
  }

  @Override
  public @NotNull CompletableFuture<OperationInvocationResult<ReadOperationResponse<?>>> invoke(
      @NotNull OperationInvocationContext context,
      @NotNull ReadOperationRequest request) {

    String uri = UriComposer.composeReadUri(resourceName, request.outputProjection());

    return null; // todo
  }
}
