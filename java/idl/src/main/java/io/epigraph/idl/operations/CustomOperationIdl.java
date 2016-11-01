package io.epigraph.idl.operations;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import io.epigraph.projections.op.path.OpVarPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CustomOperationIdl extends OperationIdl {

  protected CustomOperationIdl(
      @NotNull HttpMethod method,
      @Nullable String name,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpVarPath path,
      @Nullable OpInputModelProjection<?, ?, ?> inputProjection,
      @NotNull OpOutputVarProjection outputProjection,
      @NotNull TextLocation location) {

    super(OperationKind.CUSTOM, method, name, params, annotations,
          path, inputProjection, outputProjection, location
    );
  }
}
