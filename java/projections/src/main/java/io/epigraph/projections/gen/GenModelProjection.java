package io.epigraph.projections.gen;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenModelProjection<MP extends GenModelProjection</*MP*/?, ?>, M extends DatumType> {

  @NotNull M model();

  @Nullable MP metaProjection();

  @Nullable Annotations annotations();

  @NotNull TextLocation location();
}
