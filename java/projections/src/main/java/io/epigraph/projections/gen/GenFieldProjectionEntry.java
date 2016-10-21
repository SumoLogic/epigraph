package io.epigraph.projections.gen;

import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenFieldProjectionEntry<PD, MD, FD> {
  @NotNull RecordType.Field field();

  @NotNull GenFieldProjection<PD, MD, FD> projection();
}
