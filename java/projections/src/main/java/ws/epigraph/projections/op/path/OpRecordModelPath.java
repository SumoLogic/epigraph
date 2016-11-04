package ws.epigraph.projections.op.path;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpRecordModelPath
    extends OpModelPath<OpRecordModelPath, RecordType>
    implements GenRecordModelProjection<
    OpVarPath,
    OpTagPath,
    OpModelPath<?, ?>,
    OpRecordModelPath,
    OpFieldPathEntry,
    OpFieldPath,
    RecordType
    > {

  private static final
  ThreadLocal<IdentityHashMap<OpRecordModelPath, OpRecordModelPath>> equalsVisited =
      new ThreadLocal<>();

  @NotNull
  private Map<String, OpFieldPathEntry> fieldProjections;

  public OpRecordModelPath(
      @NotNull RecordType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpFieldPathEntry fieldProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);

    this.fieldProjections = fieldProjection == null ?
                            Collections.emptyMap() :
                            Collections.singletonMap(fieldProjection.field().name(), fieldProjection);

    ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);

    if (pathFieldProjection() == null) throw new IllegalArgumentException("Path field must be present");
  }

  @NotNull
  public Map<String, OpFieldPathEntry> fieldProjections() { return fieldProjections; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpRecordModelPath that = (OpRecordModelPath) o;

    IdentityHashMap<OpRecordModelPath, OpRecordModelPath> visitedMap = equalsVisited.get();
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
