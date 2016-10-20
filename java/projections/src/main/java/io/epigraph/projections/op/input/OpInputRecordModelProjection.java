package io.epigraph.projections.op.input;

import io.epigraph.data.RecordDatum;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputRecordModelProjection extends OpInputModelProjection<RecordType, RecordDatum> {
  private static final ThreadLocal<IdentityHashMap<OpInputRecordModelProjection, OpInputRecordModelProjection>>
      equalsVisited = new ThreadLocal<>();

  @Nullable
  private LinkedHashMap<String, OpInputFieldProjectionEntry> fieldProjections;

  public OpInputRecordModelProjection(
      @NotNull RecordType model,
      boolean required,
      @Nullable RecordDatum defaultValue,
      @Nullable Annotations annotations,
      @Nullable OpInputModelProjection<?, ?> metaProjection,
      @Nullable LinkedHashMap<String, OpInputFieldProjectionEntry> fieldProjections,
      @NotNull TextLocation location) {

    super(model, required, defaultValue, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    if (fieldProjections != null)
      ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @NotNull
  public static LinkedHashSet<OpInputFieldProjectionEntry> fields(OpInputFieldProjectionEntry... fieldProjections) {
    return new LinkedHashSet<>(Arrays.asList(fieldProjections));
  }

  @Nullable
  public LinkedHashMap<String, OpInputFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  public void addFieldProjectionEntry(@NotNull OpInputFieldProjectionEntry fieldProjection) {
    if (fieldProjections == null) fieldProjections = new LinkedHashMap<>();
    fieldProjections.put(fieldProjection.field().name(), fieldProjection);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputRecordModelProjection that = (OpInputRecordModelProjection) o;

    IdentityHashMap<OpInputRecordModelProjection, OpInputRecordModelProjection> visitedMap = equalsVisited.get();
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
