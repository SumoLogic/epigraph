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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.service.operations.Operation;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OperationSearchSuccess<O extends Operation<?, ?, ?>> implements OperationSearchResult<O> {
  @NotNull
  private final O operation;
  @NotNull
  private final Map<String, GDatum> requestParams;
  @Nullable
  private final ReqFieldPath path;
  @NotNull
  private final StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection;

  public OperationSearchSuccess(
      @NotNull final O operation,
      final @NotNull Map<String, GDatum> requestParams,
      final @Nullable ReqFieldPath path,
      final @NotNull StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection) {

    this.operation = operation;
    this.requestParams = requestParams;
    this.path = path;
    this.stepsAndProjection = stepsAndProjection;
  }

  @Contract(pure = true)
  @NotNull
  public O operation() { return operation; }

  @Contract(pure = true)
  @NotNull
  public Map<String, GDatum> requestParams() { return requestParams; }

  @Contract(pure = true)
  @Nullable
  public ReqFieldPath path() { return path; }

  @Contract(pure = true)
  public @NotNull StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection() { return stepsAndProjection; }
}
