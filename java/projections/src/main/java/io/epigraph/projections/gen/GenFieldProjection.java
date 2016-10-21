package io.epigraph.projections.gen;

import io.epigraph.projections.Annotations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenFieldProjection<PD, MD, FD> {

  @NotNull FD fieldData();

  @Nullable PD params();

  @Nullable Annotations annotations();

  @NotNull GenVarProjection<PD, MD, FD> projection();
}
