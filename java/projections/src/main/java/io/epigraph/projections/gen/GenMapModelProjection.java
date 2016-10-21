package io.epigraph.projections.gen;

import io.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenMapModelProjection<KP, M extends MapType, PD, MD, FD> extends GenModelProjection<M, PD, MD, FD> {
  @Nullable KP keyProjection();

  @NotNull GenVarProjection<PD, MD, FD> elementsProjection();
}
