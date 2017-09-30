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

package ws.epigraph.service.operations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.ReqFieldProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OperationRequest {
  private final @Nullable ReqFieldProjection path;
  private final @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection;

  protected OperationRequest(
      final @Nullable ReqFieldProjection path,
      final @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection) {

    this.path = path;
    this.outputStepsAndProjection = outputStepsAndProjection;
  }

  public @Nullable ReqFieldProjection path() { return path; }

  public @NotNull ReqFieldProjection outputProjection() { return outputStepsAndProjection.projection(); }

  public @NotNull StepsAndProjection<ReqFieldProjection> outputStepsAndProjection() {
    return outputStepsAndProjection;
  }
}
