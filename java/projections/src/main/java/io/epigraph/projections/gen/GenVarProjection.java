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
public interface GenVarProjection<PD, MD, FD> {

  @NotNull Type type();

  @NotNull LinkedHashMap<String, ? extends GenTagProjectionEntry<PD, MD, FD>> tagProjections();

  /**
   * @throws IllegalArgumentException if there's more than one tag
   */
  @Nullable GenTagProjectionEntry<PD, MD, FD> getPathTagProjection();

  @Nullable List<? extends GenVarProjection<PD, MD, FD>> polymorphicTails();

  @NotNull TextLocation location();
}
