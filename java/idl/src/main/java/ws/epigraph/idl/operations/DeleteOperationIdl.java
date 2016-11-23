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

package ws.epigraph.idl.operations;

import ws.epigraph.idl.IdlError;
import ws.epigraph.idl.ResourceIdl;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.delete.OpDeleteFieldProjection;
import ws.epigraph.projections.op.output.OpOutputFieldProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DeleteOperationIdl extends OperationIdl {
  @NotNull
  private final OpDeleteFieldProjection deleteProjection;

  protected DeleteOperationIdl(
      @Nullable String name,
      @NotNull Annotations annotations,
      @Nullable OpFieldPath path,
      @NotNull OpDeleteFieldProjection deleteProjection,
      @NotNull OpOutputFieldProjection outputProjection,
      @NotNull TextLocation location) {

    super(OperationKind.DELETE, HttpMethod.DELETE, name, annotations,
          path, null, outputProjection, location
    );

    this.deleteProjection = deleteProjection;
  }

  public @NotNull OpDeleteFieldProjection deleteProjection() { return deleteProjection; }

  @Override
  protected void validate(@NotNull ResourceIdl resource, @NotNull List<IdlError> errors) {
    super.validate(resource, errors);

    ensureProjectionStartsWithResourceType(
        resource,
        deleteProjection().varProjection(),
        "delete",
        errors
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    DeleteOperationIdl that = (DeleteOperationIdl) o;
    return Objects.equals(deleteProjection, that.deleteProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), deleteProjection);
  }
}
