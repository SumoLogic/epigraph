package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputRecordModelProjection extends ReqOutputModelProjection<RecordType> {
  private static final ThreadLocal<IdentityHashMap<ReqOutputRecordModelProjection, ReqOutputRecordModelProjection>>
      equalsVisited = new ThreadLocal<>();

  @Nullable
  private LinkedHashSet<ReqOutputFieldProjection> fieldProjections;
  @Nullable
  private Map<String, ReqOutputFieldProjection> indexedFieldProjections;

  public ReqOutputRecordModelProjection(@NotNull RecordType model,
                                        boolean required,
                                        @Nullable ReqParams params,
                                        @Nullable Annotations annotations,
                                        @Nullable ReqOutputModelProjection<?> metaProjection,
                                        @Nullable LinkedHashSet<ReqOutputFieldProjection> fieldProjections,
                                        @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    Collection<@NotNull ? extends RecordType.Field> fields = model.fields();
    if (fieldProjections != null) {
      for (ReqOutputFieldProjection fieldProjection : fieldProjections) {
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
  public static LinkedHashSet<ReqOutputFieldProjection> fields(ReqOutputFieldProjection... fieldProjections) {
    return new LinkedHashSet<>(Arrays.asList(fieldProjections));
  }

  @Nullable
  public LinkedHashSet<ReqOutputFieldProjection> fieldProjections() { return fieldProjections; }

  @Nullable
  public Map<String, ReqOutputFieldProjection> indexedFieldProjections() {
    if (indexedFieldProjections != null) return indexedFieldProjections;
    if (fieldProjections == null) return null;

    Map<String, ReqOutputFieldProjection> res = new HashMap<>();
    for (ReqOutputFieldProjection fieldProjection : fieldProjections)
      res.put(fieldProjection.field().name(), fieldProjection);

    indexedFieldProjections = res;
    return res;
  }

  public void addFieldProjection(@NotNull ReqOutputFieldProjection fieldProjection) {
    if (fieldProjections == null) fieldProjections = new LinkedHashSet<>();
    fieldProjections.add(fieldProjection);
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
    return super.hashCode() * 31 + (fieldProjections == null ? 13 : fieldProjections.size());
  }
}
