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
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.types.TypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UpdateOperationDeclaration extends OperationDeclaration {
//  public static final @NotNull String DEFAULT_NAME = "_update";

  public UpdateOperationDeclaration(
      @Nullable String name,
      @NotNull Annotations annotations,
      @Nullable OpFieldProjection path,
      @NotNull OpFieldProjection inputProjection,
      @NotNull OpFieldProjection outputProjection,
      @NotNull TextLocation location) {

    super(OperationKind.UPDATE, HttpMethod.PUT, name, annotations,
          path, inputProjection, outputProjection, location
    );
  }

//  @Override
//  protected @NotNull String defaultName() { return DEFAULT_NAME; }

  @Override
  public @NotNull OpFieldProjection inputProjection() {
    final @Nullable OpFieldProjection projection = super.inputProjection();
    assert projection != null;
    return projection;
  }

  @Override
  public @NotNull TypeApi inputType() {
    final @Nullable TypeApi inputType = super.inputType();
    assert inputType != null; // because `inputProjection` can't be null
    return inputType;
  }
}
