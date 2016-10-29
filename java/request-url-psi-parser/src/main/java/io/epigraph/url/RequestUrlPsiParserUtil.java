package io.epigraph.url;

import io.epigraph.idl.operations.ReadOperationIdl;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.op.output.OpOutputFieldProjection;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RequestUrlPsiParserUtil {

  @NotNull
  static OpOutputFieldProjection createOpFieldProjection(@NotNull ReadOperationIdl op) {
    return new OpOutputFieldProjection(
        op.params(),
        op.annotations(),
        op.outputProjection(),
        true,
        TextLocation.UNKNOWN
    );
  }
}
