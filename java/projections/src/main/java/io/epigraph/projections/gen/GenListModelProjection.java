package io.epigraph.projections.gen;

import io.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenListModelProjection<M extends ListType, PD, MD, FD> extends GenModelProjection<M, PD, MD, FD> {
  @NotNull GenVarProjection<PD, MD, FD> elementsProjection();
}
