package io.epigraph.projections.gen;

import io.epigraph.lang.TextLocation;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenTagProjectionEntry<PD, MD, FD> {

  @NotNull Type.Tag tag();

  @NotNull GenModelProjection<? extends DatumType, PD, MD, FD> projection();

  @NotNull TextLocation location();
}
