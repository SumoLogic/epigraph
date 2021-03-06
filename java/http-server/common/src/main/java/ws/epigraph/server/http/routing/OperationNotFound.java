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

package ws.epigraph.server.http.routing;

import org.jetbrains.annotations.Contract;
import ws.epigraph.service.operations.Operation;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OperationNotFound<O extends Operation<?, ?, ?>> implements OperationSearchResult<O> {
  private static final OperationNotFound<?> INSTANCE = new OperationNotFound<>();

  private OperationNotFound() {}

  @Contract(pure = true)
  @SuppressWarnings("unchecked")
  public static <O extends Operation<?, ?, ?>> OperationNotFound<O> instance() {
    return (OperationNotFound<O>) INSTANCE;
  }
}
