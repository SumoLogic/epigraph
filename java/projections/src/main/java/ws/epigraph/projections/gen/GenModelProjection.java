package ws.epigraph.projections.gen;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenModelProjection<MP extends GenModelProjection</*MP*/?, ?>, M extends DatumType> {

  @NotNull M model();

  @Nullable MP metaProjection();

  @NotNull Annotations annotations();

  @NotNull TextLocation location();
}
