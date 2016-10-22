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
   * @throws IllegalStateException if there's more than one tag
   */
  @Nullable
  default GenTagProjectionEntry pathTagProjection() throws IllegalStateException {
    @NotNull final LinkedHashMap<String, TP> tagProjections = tagProjections();
    if (tagProjections.isEmpty()) return null;
    if (tagProjections.size() > 1) throw new IllegalStateException();
    return tagProjections.values().iterator().next();
  }

  @Nullable List<VP> polymorphicTails();

  @NotNull TextLocation location();
}
