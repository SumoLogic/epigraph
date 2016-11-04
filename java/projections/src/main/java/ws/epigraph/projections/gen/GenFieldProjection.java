package ws.epigraph.projections.gen;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenFieldProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>
    > {
  @NotNull Annotations annotations();

  @NotNull VP projection(); // TODO rename to varProjection()?

  @NotNull TextLocation location();
}
