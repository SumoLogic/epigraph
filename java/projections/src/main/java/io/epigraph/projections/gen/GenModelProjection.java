package io.epigraph.projections.gen;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenModelProjection<M extends DatumType, PD, MD, FD> {

  @NotNull M model();

  @Nullable MD modelData();

  @Nullable PD params();

  @Nullable Annotations annotations();

  @NotNull TextLocation location();
}
