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
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.op.input.OpInputFieldProjection;
import ws.epigraph.projections.op.output.OpOutputFieldProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Abstract operation definition. See {@code operations.esc}.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OperationIdl {
  @NotNull
  protected final OperationKind type;
  @NotNull
  protected final HttpMethod method;
  @Nullable
  protected final String name;
  @NotNull
  protected final Annotations annotations;
  @Nullable
  protected final OpFieldPath path;
  @Nullable
  protected final OpInputFieldProjection inputProjection;
  @NotNull
  protected final OpOutputFieldProjection outputProjection;
  @NotNull
  protected final TextLocation location;

  protected OperationIdl(
      @NotNull OperationKind type,
      @NotNull HttpMethod method,
      @Nullable String name,
      @NotNull Annotations annotations,
      @Nullable OpFieldPath path,
      @Nullable OpInputFieldProjection inputProjection,
      @NotNull OpOutputFieldProjection outputProjection,
      @NotNull TextLocation location) {
    this.type = type;
    this.method = method;
    this.name = name;
    this.annotations = annotations;
    this.path = path;
    this.inputProjection = inputProjection;
    this.outputProjection = outputProjection;
    this.location = location;
  }

  @NotNull
  public OperationKind kind() { return type; }

  @NotNull
  public HttpMethod method() { return method; }

  @Nullable
  public String name() { return name; }

  public boolean isDefault() { return name == null; }

  @NotNull
  public Annotations annotations() { return annotations; }

  @Nullable
  public OpFieldPath path() { return path; }

  @Nullable
  public Type inputType() {
    return inputProjection == null ? null : inputProjection.projection().type();
  }

  public @Nullable OpInputFieldProjection inputProjection() { return inputProjection; }

  @NotNull
  public Type outputType() { return outputProjection.projection().type(); }

  @NotNull
  public OpOutputFieldProjection outputProjection() { return outputProjection; }

  @NotNull
  public TextLocation location() { return location; }

  protected void validate(@NotNull ResourceIdl resource, @NotNull List<IdlError> errors) { }

  protected void ensureProjectionStartsWithResourceType(
      @NotNull ResourceIdl resource,
      @NotNull GenVarProjection<?, ?, ?> projection,
      @NotNull String projectionName,
      @NotNull List<IdlError> errors) {

    @NotNull Type outputType = resource.fieldType().type;
    if (path != null) outputType = ProjectionUtils.tipType(path.projection()).type;

    final Type outputProjectionType = projection.type();

    if (!outputType.equals(outputProjectionType))
      errors.add(
          new IdlError(
              resource,
              this,
              String.format(
                  "Operation kind '%s' != %s projection kind '%s'",
                  outputType.name(),
                  projectionName,
                  outputProjectionType.name()
              ),
              outputProjection.location()
          )
      );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationIdl that = (OperationIdl) o;
    return type == that.type &&
           Objects.equals(name, that.name) &&
           Objects.equals(annotations, that.annotations) &&
           Objects.equals(path, that.path) &&
           Objects.equals(inputProjection, that.inputProjection) &&
           Objects.equals(outputProjection, that.outputProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, annotations, path, inputProjection, outputProjection);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    if (isDefault()) sb.append("default ");
    else sb.append(name).append(": ");

    sb.append(type);

    return sb.toString();
  }
}
