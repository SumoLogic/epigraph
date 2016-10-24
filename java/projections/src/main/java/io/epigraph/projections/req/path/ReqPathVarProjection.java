package io.epigraph.projections.req.path;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.abs.AbstractVarProjection;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathVarProjection extends AbstractVarProjection<
    ReqPathVarProjection,
    ReqPathTagProjectionEntry,
    ReqPathModelProjection<?, ?>
    > {

  public ReqPathVarProjection(
      @NotNull Type type,
      @NotNull ReqPathTagProjectionEntry tagProjection,
      @Nullable List<ReqPathVarProjection> polymorphicTails,
      @NotNull TextLocation location) {
    super(type, Collections.singletonMap(tagProjection.tag().name(), tagProjection), polymorphicTails, location);
  }
}
