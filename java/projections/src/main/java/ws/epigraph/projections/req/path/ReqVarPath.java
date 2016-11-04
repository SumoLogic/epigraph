package ws.epigraph.projections.req.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqVarPath extends AbstractVarProjection<
    ReqVarPath,
    ReqTagPath,
    ReqModelPath<?, ?>
    > {

  public ReqVarPath(
      @NotNull Type type,
      @Nullable ReqTagPath tagProjection,
      @NotNull TextLocation location) {
    super(type,
          tagProjection == null ? Collections.emptyMap()
                                : Collections.singletonMap(tagProjection.tag().name(), tagProjection),
          null,
          location
    );
  }
}
