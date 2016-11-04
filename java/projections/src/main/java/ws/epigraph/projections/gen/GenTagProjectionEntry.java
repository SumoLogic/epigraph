package ws.epigraph.projections.gen;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenTagProjectionEntry<MP extends GenModelProjection</*MP*/?, ?>> {

  @NotNull Type.Tag tag();

  @NotNull MP projection();

  @NotNull TextLocation location();
}
