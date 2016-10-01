package io.epigraph.idl.operations;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.output.OpOutputVarProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadOperation extends Operation {
  protected ReadOperation(@Nullable String name,
                          @Nullable OpParams params,
                          @Nullable Annotations annotations,
                          @NotNull OpOutputVarProjection outputProjection,
                          @NotNull TextLocation location) {
    super(OperationType.READ, name, params, annotations, outputProjection, location);
  }
}
