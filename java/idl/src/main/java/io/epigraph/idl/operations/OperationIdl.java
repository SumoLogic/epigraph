package io.epigraph.idl.operations;

import io.epigraph.idl.IdlError;
import io.epigraph.idl.ResourceIdl;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.gen.GenVarProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import io.epigraph.projections.op.path.OpVarPath;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
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
  protected final OpParams params;
  @NotNull
  protected final Annotations annotations;
  @Nullable
  protected final OpVarPath path;
  @Nullable
  protected final OpInputModelProjection<?, ?, ?> inputProjection;
  @NotNull
  protected final OpOutputVarProjection outputProjection;
  @NotNull
  protected final TextLocation location;

  protected OperationIdl(
      @NotNull OperationKind type,
      @NotNull HttpMethod method,
      @Nullable String name,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpVarPath path,
      @Nullable OpInputModelProjection<?, ?, ?> inputProjection,
      @NotNull OpOutputVarProjection outputProjection,
      @NotNull TextLocation location) {
    this.type = type;
    this.method = method;
    this.name = name;
    this.params = params;
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
  public OpParams params() { return params; }

  @NotNull
  public Annotations annotations() { return annotations; }

  @Nullable
  public OpVarPath path() { return path; }

  @Nullable
  public DatumType inputType() {
    return inputProjection == null ? null : inputProjection.model();
  }

  @Nullable
  public OpInputModelProjection<?, ?, ?> inputProjection() { return inputProjection; }

  @NotNull
  public Type outputType() { return outputProjection.type(); }

  @NotNull
  public OpOutputVarProjection outputProjection() { return outputProjection; }

  @NotNull
  public TextLocation location() { return location; }

  protected void validate(@NotNull ResourceIdl resource, @NotNull List<IdlError> errors) { }

  protected void ensureProjectionStartsWithResourceType(
      @NotNull ResourceIdl resource,
      @NotNull GenVarProjection<?, ?, ?> projection,
      @NotNull String projectionName,
      @NotNull List<IdlError> errors) {

    @NotNull Type outputType = resource.fieldType().type;
    if (path != null) outputType = ProjectionUtils.tipType(path).type;

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
           Objects.equals(params, that.params) &&
           Objects.equals(annotations, that.annotations) &&
           Objects.equals(path, that.path) &&
           Objects.equals(inputProjection, that.inputProjection) &&
           Objects.equals(outputProjection, that.outputProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, params, annotations, path, inputProjection, outputProjection);
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
