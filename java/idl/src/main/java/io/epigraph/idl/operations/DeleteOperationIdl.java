package io.epigraph.idl.operations;

import io.epigraph.idl.IdlError;
import io.epigraph.idl.ResourceIdl;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.delete.OpDeleteVarProjection;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import io.epigraph.projections.op.path.OpVarPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DeleteOperationIdl extends OperationIdl {
  @NotNull
  private final OpDeleteVarProjection deleteProjection;

  protected DeleteOperationIdl(
      @Nullable String name,
      @Nullable OpParams params,
      @Nullable Annotations annotations,
      @Nullable OpVarPath path,
      @NotNull OpDeleteVarProjection deleteProjection,
      @NotNull OpOutputVarProjection outputProjection,
      @NotNull TextLocation location) {

    super(OperationKind.DELETE, name, params, annotations,
          path, null, outputProjection, location
    );

    this.deleteProjection = deleteProjection;
  }

  @NotNull
  public OpDeleteVarProjection deleteProjection() { return deleteProjection; }

  @Override
  protected void validate(@NotNull ResourceIdl resource, @NotNull List<IdlError> errors) {
    super.validate(resource, errors);

    ensureProjectionStartsWithResourceType(
        resource,
        deleteProjection(),
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
