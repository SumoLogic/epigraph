package ws.epigraph.projections.op.input;

import ws.epigraph.data.RecordDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputRecordModelProjection
    extends OpInputModelProjection<OpInputRecordModelProjection, RecordType, RecordDatum>
    implements GenRecordModelProjection<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?>,
    OpInputRecordModelProjection,
    OpInputFieldProjectionEntry,
    OpInputFieldProjection,
    RecordType
    > {

  private static final ThreadLocal<IdentityHashMap<OpInputRecordModelProjection, OpInputRecordModelProjection>>
      equalsVisited = new ThreadLocal<>();

  @NotNull
  private Map<String, OpInputFieldProjectionEntry> fieldProjections;

  public OpInputRecordModelProjection(
      @NotNull RecordType model,
      boolean required,
      @Nullable RecordDatum defaultValue,
      @NotNull Annotations annotations,
      @Nullable OpInputRecordModelProjection metaProjection,
      @NotNull Map<String, OpInputFieldProjectionEntry> fieldProjections,
      @NotNull TextLocation location) {

    super(model, required, defaultValue, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @NotNull
  public static LinkedHashSet<OpInputFieldProjectionEntry> fields(OpInputFieldProjectionEntry... fieldProjections) {
    return new LinkedHashSet<>(Arrays.asList(fieldProjections));
  }

  @NotNull
  public Map<String, OpInputFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  public void addFieldProjectionEntry(@NotNull OpInputFieldProjectionEntry fieldProjection) {
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
    return super.hashCode() * 31 + fieldProjections.size();
  }
}
