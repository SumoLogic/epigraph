package io.epigraph.idl.operations;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.op.output.OpOutputFieldProjection;
import io.epigraph.projections.op.path.OpFieldPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CreateOperationIdl extends OperationIdl {
  protected CreateOperationIdl(
      @Nullable String name,
      @NotNull Annotations annotations,
      @Nullable OpFieldPath path,
      @NotNull OpInputModelProjection<?, ?, ?> inputProjection,
      @NotNull OpOutputFieldProjection outputProjection,
      @NotNull TextLocation location) {
    super(
        OperationKind.CREATE, HttpMethod.POST, name, annotations,
        path, inputProjection, outputProjection, location
    );
  }
}
