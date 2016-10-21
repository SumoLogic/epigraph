package io.epigraph.projections.gen;

import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenRecordModelProjection<M extends RecordType, PD, MD, FD> extends GenModelProjection<M, PD, MD, FD> {

  @NotNull LinkedHashMap<String, GenFieldProjectionEntry> fieldProjections();

  /**
   * @throws IllegalArgumentException if there's more than one field
   */
  @Nullable GenFieldProjectionEntry getPathFieldProjection();
}
