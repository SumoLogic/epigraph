package io.epigraph.projections.gen;

import io.epigraph.lang.TextLocation;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenFieldProjectionEntry<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>,
    FP extends GenFieldProjection<VP, TP, MP>
    > {
  @NotNull RecordType.Field field();

  @NotNull FP projection(); // TODO rename to fieldProjection()?

  @NotNull TextLocation location();
}
