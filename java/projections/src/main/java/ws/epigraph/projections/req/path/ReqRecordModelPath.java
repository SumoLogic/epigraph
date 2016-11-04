package ws.epigraph.projections.req.path;

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
public class ReqRecordModelPath
    extends ReqModelPath<ReqRecordModelPath, RecordType>
    implements GenRecordModelProjection<
    ReqVarPath,
    ReqTagPath,
    ReqModelPath<?, ?>,
    ReqRecordModelPath,
    ReqFieldPathEntry,
    ReqFieldPath,
    RecordType
    > {

  private static final ThreadLocal<IdentityHashMap<ReqRecordModelPath, ReqRecordModelPath>>
      equalsVisited = new ThreadLocal<>();

  @NotNull
  private Map<String, ReqFieldPathEntry> fieldProjections;

  public ReqRecordModelPath(
      @NotNull RecordType model,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull ReqFieldPathEntry fieldProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
    this.fieldProjections = Collections.singletonMap(fieldProjection.field().name(), fieldProjection);

    Collection<@NotNull ? extends RecordType.Field> fields = model.fields();
    ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);

    if (pathFieldProjection() == null) throw new IllegalArgumentException("Path field must be present");
  }

  @NotNull
  public Map<String, ReqFieldPathEntry> fieldProjections() { return fieldProjections; }

  @Nullable
  public ReqFieldPathEntry fieldProjection(@NotNull String fieldName) {
    return fieldProjections.get(fieldName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqRecordModelPath that = (ReqRecordModelPath) o;

    IdentityHashMap<ReqRecordModelPath, ReqRecordModelPath> visitedMap = equalsVisited.get();
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
