package io.epigraph.projections.op.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputRecordModelProjection extends OpOutputModelProjection<RecordType> {
  private static final ThreadLocal<IdentityHashMap<OpOutputRecordModelProjection, OpOutputRecordModelProjection>>
      equalsVisited = new ThreadLocal<>();

  @Nullable
  private LinkedHashSet<OpOutputFieldProjection> fieldProjections;
  @Nullable
  private Map<String, OpOutputFieldProjection> indexedFieldProjections;

  public OpOutputRecordModelProjection(@NotNull RecordType model,
                                       boolean includeInDefault,
                                       @Nullable OpParams params,
                                       @Nullable Annotations annotations,
                                       @Nullable OpOutputModelProjection<?> metaProjection,
                                       @Nullable LinkedHashSet<OpOutputFieldProjection> fieldProjections,
                                       @NotNull TextLocation location) {
    super(model, includeInDefault, params, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    Collection<@NotNull ? extends RecordType.Field> fields = model.fields();
    if (fieldProjections != null) {
      for (OpOutputFieldProjection fieldProjection : fieldProjections) {
        RecordType.Field field = fieldProjection.field();
        if (!fields.contains(field))
          throw new IllegalArgumentException(
              String.format("Field '%s' does not belong to record model '%s'. Known fields: %s",
                            field.name(), model.name(), listFields(fields)
              )
          );
      }
    }
  }

  private static String listFields(@NotNull Collection<? extends RecordType.Field> fields) {
    return fields.stream().map(RecordType.Field::name).collect(Collectors.joining(", "));
  }

  @NotNull
  public static LinkedHashSet<OpOutputFieldProjection> fields(OpOutputFieldProjection... fieldProjections) {
    return new LinkedHashSet<>(Arrays.asList(fieldProjections));
  }

  @Nullable
  public LinkedHashSet<OpOutputFieldProjection> fieldProjections() { return fieldProjections; }

  @Nullable
  public Map<String, OpOutputFieldProjection> indexedFieldProjections() {
    if (indexedFieldProjections != null) return indexedFieldProjections;
    if (fieldProjections == null) return null;

    Map<String, OpOutputFieldProjection> res = new HashMap<>();
    for (OpOutputFieldProjection fieldProjection : fieldProjections)
      res.put(fieldProjection.field().name(), fieldProjection);

    indexedFieldProjections = res;
    return res;
  }

  public void addFieldProjection(@NotNull OpOutputFieldProjection fieldProjection) {
    if (fieldProjections == null) fieldProjections = new LinkedHashSet<>();
    fieldProjections.add(fieldProjection);
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
