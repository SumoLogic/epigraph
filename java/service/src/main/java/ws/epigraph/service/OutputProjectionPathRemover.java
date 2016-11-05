package ws.epigraph.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.req.output.*;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OutputProjectionPathRemover { // todo move somewhere

  public static PathRemovalResult removePath(@NotNull ReqOutputVarProjection projection, int steps)
      throws AmbiguousPathException {

    if (steps == 0) return new PathRemovalResult(projection, null);

    if (projection.tagProjections().size() > 1) throw new AmbiguousPathException();
    final @Nullable ReqOutputTagProjectionEntry tagProjection = projection.pathTagProjection();

    if (tagProjection == null) return PathRemovalResult.NULL;

    return removePath(tagProjection.projection(), steps - 1);
  }

  public static PathRemovalResult removePath(@NotNull ReqOutputModelProjection<?, ?> projection, int steps)
      throws AmbiguousPathException {
    if (steps == 0) return new PathRemovalResult(null, projection);

    switch (projection.model().kind()) {
      
      case RECORD:
        ReqOutputRecordModelProjection rmp = (ReqOutputRecordModelProjection) projection;
        final @NotNull Map<String, ReqOutputFieldProjectionEntry> fieldProjections = rmp.fieldProjections();
        
        if (fieldProjections.size() > 1) throw new AmbiguousPathException();
        @Nullable final ReqOutputFieldProjectionEntry fieldProjection = rmp.pathFieldProjection();
        
        if (fieldProjection == null) return PathRemovalResult.NULL;

        return removePath(fieldProjection.projection().projection(), steps - 1);
      
      case MAP:
        ReqOutputMapModelProjection mmp = (ReqOutputMapModelProjection) projection;
        final @Nullable List<ReqOutputKeyProjection> keys = mmp.keys();
        if (keys == null) return PathRemovalResult.NULL;
        if (keys.size() != 1) throw new AmbiguousPathException();

        return removePath(mmp.itemsProjection(), steps - 1);
    }
    
    throw new AmbiguousPathException();
  }

  public static class PathRemovalResult {
    public static final PathRemovalResult NULL = new PathRemovalResult(null, null);

    @Nullable
    private final ReqOutputVarProjection varProjection;
    @Nullable
    private final ReqOutputModelProjection<?, ?> modelProjection;

    PathRemovalResult(
        final @Nullable ReqOutputVarProjection varProjection,
        final @Nullable ReqOutputModelProjection<?, ?> modelProjection) {

      this.varProjection = varProjection;
      this.modelProjection = modelProjection;
    }

    @Nullable
    public ReqOutputVarProjection varProjection() { return varProjection; }

    @Nullable
    public ReqOutputModelProjection<?, ?> modelProjection() { return modelProjection; }
  }
}
