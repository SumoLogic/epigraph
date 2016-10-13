package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputRecordModelProjection extends OpOutputModelProjection<RecordType> {
  private static final ThreadLocal<IdentityHashMap<OpOutputRecordModelProjection, OpOutputRecordModelProjection>>
      equalsVisited = new ThreadLocal<>();

  @Nullable
  private LinkedHashMap<String, OpOutputFieldProjection> fieldProjections;

  public OpOutputRecordModelProjection(@NotNull RecordType model,
                                       boolean includeInDefault,
                                       @Nullable OpParams params,
                                       @Nullable Annotations annotations,
                                       @Nullable OpOutputModelProjection<?> metaProjection,
                                       @Nullable LinkedHashMap<String, OpOutputFieldProjection> fieldProjections,
                                       @NotNull TextLocation location) {
    super(model, includeInDefault, params, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    if (fieldProjections != null)
      ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @NotNull
  public static LinkedHashSet<OpOutputFieldProjection> fields(OpOutputFieldProjection... fieldProjections) {
    return new LinkedHashSet<>(Arrays.asList(fieldProjections));
  }

  public @Nullable LinkedHashMap<String, OpOutputFieldProjection> fieldProjections() { return fieldProjections; }

  public void addFieldProjection(@NotNull String fieldName, @NotNull OpOutputFieldProjection fieldProjection) {
    if (fieldProjections == null) fieldProjections = new LinkedHashMap<>();
    fieldProjections.put(fieldName, fieldProjection);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpOutputRecordModelProjection that = (OpOutputRecordModelProjection) o;

    IdentityHashMap<OpOutputRecordModelProjection, OpOutputRecordModelProjection> visitedMap = equalsVisited.get();
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
    return super.hashCode() * 31 + (fieldProjections == null ? 13 : fieldProjections.size());
  }
}
