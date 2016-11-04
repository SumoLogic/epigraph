package ws.epigraph.projections.gen;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenVarProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>
    > {

  @NotNull Type type();

  @NotNull Map<String, TP> tagProjections();

  /**
   * @return single tag if there's just one; {@code null} otherwise
   */
  @Nullable
  default TP pathTagProjection() {
    @NotNull final Map<String, TP> tagProjections = tagProjections();
    if (tagProjections.size() == 1) return tagProjections.values().iterator().next();
    else return null;
  }

  @Nullable List<VP> polymorphicTails();

  @NotNull TextLocation location();
}
