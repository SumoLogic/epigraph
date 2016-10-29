package io.epigraph.idl.operations;

import io.epigraph.idl.IdlError;
import io.epigraph.idl.ResourceIdl;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import io.epigraph.projections.op.path.OpVarPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadOperationIdl extends OperationIdl {
  protected ReadOperationIdl(
      @Nullable String name,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpVarPath path,
      @NotNull OpOutputVarProjection outputProjection,
      @NotNull TextLocation location) {

    super(OperationKind.READ, name, params, annotations,
          path, null, outputProjection, location
    );
  }

  @Override
  protected void validate(@NotNull ResourceIdl resource, @NotNull List<IdlError> errors) {
    super.validate(resource, errors);

    ensureProjectionStartsWithResourceType(
        resource,
        outputProjection(),
        "output",
        errors
    );

  }
}
