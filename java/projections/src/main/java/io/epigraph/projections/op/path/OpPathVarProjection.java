package io.epigraph.projections.op.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractVarProjection;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPathVarProjection extends AbstractVarProjection<
    OpPathVarProjection,
    OpPathTagProjectionEntry,
    OpPathModelProjection<?, ?>
    > {
  public OpPathVarProjection(
      @NotNull Type type,
      @Nullable OpPathTagProjectionEntry tagProjection,
      @NotNull TextLocation location) {
    super(
        type,
        tagProjection == null ? Collections.emptyMap()
                              : Collections.singletonMap(tagProjection.tag().name(), tagProjection),
        null,
        location
    );
  }
}
