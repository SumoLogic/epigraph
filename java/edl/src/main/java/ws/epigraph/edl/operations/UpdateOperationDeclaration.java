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

package ws.epigraph.edl.operations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.input.OpInputFieldProjection;
import ws.epigraph.projections.op.output.OpOutputFieldProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.types.Type;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UpdateOperationDeclaration extends OperationDeclaration {
  protected UpdateOperationDeclaration(
      @Nullable String name,
      @NotNull Annotations annotations,
      @Nullable OpFieldPath path,
      @NotNull OpInputFieldProjection inputProjection,
      @NotNull OpOutputFieldProjection outputProjection,
      @NotNull TextLocation location) {

    super(OperationKind.UPDATE, HttpMethod.PUT, name, annotations,
          path, inputProjection, outputProjection, location
    );
  }

  @Override
  public @NotNull OpInputFieldProjection inputProjection() {
    final @Nullable OpInputFieldProjection projection = super.inputProjection();
    assert projection != null;
    return projection;
  }

  @Override
  public @NotNull Type inputType() {
    final @Nullable Type inputType = super.inputType();
    assert inputType != null; // because `inputProjection` can't be null
    return inputType;
  }
}
