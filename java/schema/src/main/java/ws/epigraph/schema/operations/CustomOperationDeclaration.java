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

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CustomOperationDeclaration extends OperationDeclaration {

  public CustomOperationDeclaration(
      @NotNull HttpMethod method,
      @Nullable String name,
      @NotNull Annotations annotations,
      @Nullable OpFieldProjection path,
      @Nullable OpFieldProjection inputProjection,
      @NotNull OpFieldProjection outputProjection,
      @NotNull TextLocation location) {

    super(OperationKind.CUSTOM, method, name, annotations,
          path, inputProjection, outputProjection, location
    );
  }

  @Override
  protected @NotNull String defaultName() {
    throw new RuntimeException("unreachable"); // custom operations should always have names
  }
}
