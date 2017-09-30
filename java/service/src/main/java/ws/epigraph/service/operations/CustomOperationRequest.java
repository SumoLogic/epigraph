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
import ws.epigraph.data.Data;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.ReqFieldProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CustomOperationRequest extends OperationRequest {
  private final @Nullable Data data;
  private final @Nullable StepsAndProjection<ReqFieldProjection> inputStepsAndProjection;

  public CustomOperationRequest(
      final @Nullable ReqFieldProjection path,
      final @Nullable Data data,
      final @Nullable StepsAndProjection<ReqFieldProjection> inputStepsAndProjection,
      final @NotNull StepsAndProjection<ReqFieldProjection> outputStepsProjection) {
    super(path, outputStepsProjection);
    this.data = data;
    this.inputStepsAndProjection = inputStepsAndProjection;
  }

  public @Nullable Data data() { return data; }

  public @Nullable StepsAndProjection<ReqFieldProjection> inputStepsAndProjection() { return inputStepsAndProjection; }

  public @Nullable ReqFieldProjection inputProjection() {
    return inputStepsAndProjection == null ? null : inputStepsAndProjection.projection();
  }
}
