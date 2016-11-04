package ws.epigraph.projections.op.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpVarPath extends AbstractVarProjection<
    OpVarPath,
    OpTagPath,
    OpModelPath<?, ?>
    > {
  public OpVarPath(
      @NotNull Type type,
      @Nullable OpTagPath tagProjection,
      @NotNull TextLocation location) {
    super(
        type,
        tagProjection == null ? Collections.emptyMap()
                              : Collections.singletonMap(tagProjection.tag().name(), tagProjection),
        null,
        location
    );
  }

  @Contract("null -> true")
  public static boolean isEnd(@Nullable OpVarPath path) {
    return path == null || path.tagProjections().isEmpty();
  }
}
