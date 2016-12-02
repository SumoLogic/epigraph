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

package ws.epigraph.service.operations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.req.delete.ReqDeleteFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DeleteOperationRequest extends OperationRequest {
  @NotNull
  private final ReqDeleteFieldProjection DeleteProjection;

  public DeleteOperationRequest(
      final @Nullable ReqFieldPath path,
      final @NotNull ReqDeleteFieldProjection DeleteProjection,
      final @NotNull ReqOutputFieldProjection outputProjection) {

    super(path, outputProjection);
    this.DeleteProjection = DeleteProjection;
  }

  @NotNull
  public ReqDeleteFieldProjection deleteProjection() { return DeleteProjection; }
}
