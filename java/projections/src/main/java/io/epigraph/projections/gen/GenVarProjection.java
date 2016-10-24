package io.epigraph.projections.gen;

import io.epigraph.lang.TextLocation;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenVarProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>
    > {

  @NotNull Type type();

  @NotNull LinkedHashMap<String, TP> tagProjections();

  /**
   * @return single tag if there's just one; {@code null} otherwise
   */
  @Nullable
  default GenTagProjectionEntry pathTagProjection() {
    @NotNull final LinkedHashMap<String, TP> tagProjections = tagProjections();
    if (tagProjections.size() == 1) return tagProjections.values().iterator().next();
    else return null;
  }

  @Nullable List<VP> polymorphicTails();

  @NotNull TextLocation location();
}
