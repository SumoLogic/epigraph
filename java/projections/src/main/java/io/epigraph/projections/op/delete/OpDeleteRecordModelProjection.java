package io.epigraph.projections.op.delete;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.gen.GenRecordModelProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteRecordModelProjection
    extends OpDeleteModelProjection<OpDeleteRecordModelProjection, RecordType>
    implements GenRecordModelProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>,
    OpDeleteRecordModelProjection,
    OpDeleteFieldProjectionEntry,
    OpDeleteFieldProjection,
    RecordType
    > {

  private static final
  ThreadLocal<IdentityHashMap<OpDeleteRecordModelProjection, OpDeleteRecordModelProjection>> equalsVisited =
      new ThreadLocal<>();

  @NotNull
  private Map<String, OpDeleteFieldProjectionEntry> fieldProjections;

  public OpDeleteRecordModelProjection(
      @NotNull RecordType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpDeleteRecordModelProjection metaProjection,
      @NotNull Map<String, OpDeleteFieldProjectionEntry> fieldProjections,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @NotNull
  public Map<String, OpDeleteFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpDeleteRecordModelProjection that = (OpDeleteRecordModelProjection) o;

    IdentityHashMap<OpDeleteRecordModelProjection, OpDeleteRecordModelProjection> visitedMap = equalsVisited.get();
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
