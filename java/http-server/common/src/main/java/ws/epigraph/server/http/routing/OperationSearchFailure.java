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
import org.jetbrains.annotations.NotNull;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.service.operations.Operation;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OperationSearchFailure<O extends Operation<?, ?, ?>> implements OperationSearchResult<O> {
  private final @NotNull Map<O, List<PsiProcessingError>> errors;

  public OperationSearchFailure(final @NotNull Map<O, List<PsiProcessingError>> errors) { this.errors = errors; }

  @Contract(pure = true)
  public @NotNull Map<O, List<PsiProcessingError>> errors() { return errors; }
}
