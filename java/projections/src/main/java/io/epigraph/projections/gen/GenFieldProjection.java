package io.epigraph.projections.gen;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenFieldProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>
    > {
  @NotNull Annotations annotations();

  @NotNull VP projection();

  @NotNull TextLocation location();
}
