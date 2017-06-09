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

package ws.epigraph.schema;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.types.TypeApi;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TransformerDeclaration {
  private final @NotNull String name;
  private final @NotNull TypeApi type;
  private final @NotNull OpInputVarProjection inputProjection;
  private final @NotNull OpOutputVarProjection outputProjection;
  private final @NotNull Annotations annotations;
  private final @NotNull TextLocation location;

  public TransformerDeclaration(
      @NotNull String name,
      @NotNull TypeApi type,
      @NotNull Annotations annotations,
      @NotNull OpInputVarProjection inputProjection,
      @NotNull OpOutputVarProjection outputProjection,
      @NotNull TextLocation location) {

    this.name = name;
    this.type = type;
    this.inputProjection = inputProjection;
    this.outputProjection = outputProjection;
    this.annotations = annotations;
    this.location = location;

    if (!inputProjection.type().equals(type))
      throw new IllegalArgumentException(
          String.format("'%s' transformer type '%s' differs from input projection type '%s'",
              name, type.name(), inputProjection.type().name()
          )
      );

    if (!outputProjection.type().equals(type))
      throw new IllegalArgumentException(
          String.format("'%s' transformer type '%s' differs from output projection type '%s'",
              name, type.name(), outputProjection.type().name()
          )
      );
  }

  @Contract(pure = true)
  public @NotNull String name() { return name; }

  @Contract(pure = true)
  public @NotNull TypeApi type() { return type; }

  @Contract(pure = true)
  public @NotNull OpInputVarProjection inputProjection() { return inputProjection; }

  @Contract(pure = true)
  public @NotNull OpOutputVarProjection outputProjection() { return outputProjection; }

  @Contract(pure = true)
  public @NotNull Annotations annotations() { return annotations; }

  @Contract(pure = true)
  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final TransformerDeclaration that = (TransformerDeclaration) o;
    return Objects.equals(name, that.name) &&
           Objects.equals(type, that.type) &&
           Objects.equals(inputProjection, that.inputProjection) &&
           Objects.equals(outputProjection, that.outputProjection) &&
           Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type, inputProjection, outputProjection, annotations);
  }
}
