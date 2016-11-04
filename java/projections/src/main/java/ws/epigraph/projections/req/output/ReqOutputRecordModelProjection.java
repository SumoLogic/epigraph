package ws.epigraph.projections.req.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputRecordModelProjection
    extends ReqOutputModelProjection<ReqOutputRecordModelProjection, RecordType>
    implements GenRecordModelProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?>,
    ReqOutputRecordModelProjection,
    ReqOutputFieldProjectionEntry,
    ReqOutputFieldProjection,
    RecordType
    > {
  private static final ThreadLocal<IdentityHashMap<ReqOutputRecordModelProjection, ReqOutputRecordModelProjection>>
      equalsVisited = new ThreadLocal<>();

  @NotNull
  private Map<String, ReqOutputFieldProjectionEntry> fieldProjections;

  public ReqOutputRecordModelProjection(
      @NotNull RecordType model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputRecordModelProjection metaProjection,
      @NotNull Map<String, ReqOutputFieldProjectionEntry> fieldProjections,
      @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @NotNull
  public Map<String, ReqOutputFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  @Nullable
  public ReqOutputFieldProjectionEntry fieldProjection(@NotNull String fieldName) {
    return fieldProjections.get(fieldName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputRecordModelProjection that = (ReqOutputRecordModelProjection) o;

    IdentityHashMap<ReqOutputRecordModelProjection, ReqOutputRecordModelProjection> visitedMap = equalsVisited.get();
    boolean mapWasNull = visitedMap == null;
    if (mapWasNull) {
      visitedMap = new IdentityHashMap<>();
      equalsVisited.set(visitedMap);
    } else {
      if (that == visitedMap.get(this)) return true;
      if (visitedMap.containsKey(this)) return false;
    }
    visitedMap.put(this, that);
    boolean res = Objects.equals(fieldProjections, that.fieldProjections);
    if (mapWasNull) equalsVisited.remove();
    return res;
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 31 + fieldProjections.size();
  }
}
