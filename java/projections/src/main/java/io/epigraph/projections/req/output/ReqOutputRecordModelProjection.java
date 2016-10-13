package io.epigraph.projections.req.output;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputRecordModelProjection extends ReqOutputModelProjection<RecordType> {
  private static final ThreadLocal<IdentityHashMap<ReqOutputRecordModelProjection, ReqOutputRecordModelProjection>>
      equalsVisited = new ThreadLocal<>();

  @Nullable
  private LinkedHashMap<String, ReqOutputFieldProjection> fieldProjections;

  public ReqOutputRecordModelProjection(@NotNull RecordType model,
                                        boolean required,
                                        @Nullable ReqParams params,
                                        @Nullable Annotations annotations,
                                        @Nullable ReqOutputModelProjection<?> metaProjection,
                                        @Nullable LinkedHashMap<String, ReqOutputFieldProjection> fieldProjections,
                                        @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    Collection<@NotNull ? extends RecordType.Field> fields = model.fields();
    if (fieldProjections != null)
      ProjectionUtils.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @NotNull
  public static LinkedHashSet<ReqOutputFieldProjection> fields(ReqOutputFieldProjection... fieldProjections) {
    return new LinkedHashSet<>(Arrays.asList(fieldProjections));
  }

  public @Nullable LinkedHashMap<String, ReqOutputFieldProjection> fieldProjections() { return fieldProjections; }

  @Nullable
  public ReqOutputFieldProjection fieldProjection(@NotNull String fieldName) {
    return fieldProjections == null ? null : fieldProjections.get(fieldName);
  }

  public void addFieldProjection(@NotNull String fieldName, @NotNull ReqOutputFieldProjection fieldProjection) {
    if (fieldProjections == null) fieldProjections = new LinkedHashMap<>();
    fieldProjections.put(fieldName, fieldProjection);
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
