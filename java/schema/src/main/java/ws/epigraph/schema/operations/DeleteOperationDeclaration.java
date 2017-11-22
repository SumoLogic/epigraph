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

package ws.epigraph.schema.operations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.ResourceDeclarationError;
import ws.epigraph.types.TypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DeleteOperationDeclaration extends OperationDeclaration {
//  public static final @NotNull String DEFAULT_NAME = "_delete";

  public DeleteOperationDeclaration(
      @Nullable String name,
      @NotNull Annotations annotations,
      @Nullable OpFieldProjection path,
      @NotNull OpFieldProjection deleteProjection,
      @NotNull OpFieldProjection outputProjection,
      @NotNull TextLocation location) {

    super(OperationKind.DELETE, HttpMethod.DELETE, name, annotations,
        path, deleteProjection, outputProjection, location
    );

    if (path != null) {
      TypeApi tipType = ProjectionUtils.tipType(path.projection()).type();
      TypeApi deleteType = deleteProjection.projection().type();

      if (!deleteType.isAssignableFrom(tipType))
        throw new IllegalArgumentException(
            String.format("'%s' %s operation: output projection type %s is not assignable from path tip type %s",
                nameOrDefaultName(), kind(), deleteType.name(), tipType.name()
            )
        );
    }
  }

//  @Override
//  protected @NotNull String defaultName() { return DEFAULT_NAME; }

  public @NotNull OpFieldProjection deleteProjection() {
    OpFieldProjection inputProjection = inputProjection();
    assert inputProjection != null;
    return inputProjection;
  }

  @Override
  public void validate(@NotNull ResourceDeclaration resource, @NotNull List<ResourceDeclarationError> errors) {
    super.validate(resource, errors);

    ensureProjectionStartsWithResourceType(
        resource,
        deleteProjection().projection(),
        "delete",
        errors
    );
  }

}
