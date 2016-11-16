/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.server.http.routing;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.service.operations.Operation;
import ws.epigraph.url.RequestUrl;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OperationSearchSuccess<O extends Operation<?, ?, ?>, R extends RequestUrl>
    implements OperationSearchResult<O> {
  @NotNull
  private final O operation;
  @NotNull
  private final R requestUrl;

  public OperationSearchSuccess(
      @NotNull final O operation,
      @NotNull final R url) {

    this.operation = operation;
    requestUrl = url;
  }

  @Contract(pure = true)
  @NotNull
  public O operation() { return operation; }

  @NotNull
  public R requestUrl() { return requestUrl; }
}
