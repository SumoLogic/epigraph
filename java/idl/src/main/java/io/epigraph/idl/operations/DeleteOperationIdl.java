package io.epigraph.idl.operations;

import io.epigraph.idl.IdlError;
import io.epigraph.idl.ResourceIdl;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.delete.OpDeleteFieldProjection;
import io.epigraph.projections.op.output.OpOutputFieldProjection;
import io.epigraph.projections.op.path.OpFieldPath;
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
        deleteProjection().projection(),
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
