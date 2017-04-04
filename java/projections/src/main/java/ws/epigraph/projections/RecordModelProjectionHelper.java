/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.projections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenFieldProjection;
import ws.epigraph.projections.gen.GenFieldProjectionEntry;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.types.FieldApi;
import ws.epigraph.types.RecordTypeApi;
import ws.epigraph.types.TypeApi;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class for {@link ws.epigraph.projections.gen.GenRecordModelProjection} implementations.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class RecordModelProjectionHelper {
  // this ought to be an abstract base class, but Java doesn't support multiple inheritance.
  // Concrete implementation would have to extend both abstract record model class and model projection base class

  private static final
  ThreadLocal<IdentityHashMap<GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?>, GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?>>>
      equalsVisited = new ThreadLocal<>();

  private RecordModelProjectionHelper() {}

  /**
   * Recursion-aware helper for implementing {@code equals}.
   * <p/>
   * Usage pattern:
   * <blockquote><pre><code>
   *   boolean equals(Object o) {
   *     if (!super.equals(o)) return false;
   *     return RecordModelProjectionHelper.equals(this, o);
   *   }
   * </code></pre></blockquote>
   *
   * @param rmp {@code this} class
   * @param o   object to compare to
   * @return {@code true} iff equals
   */
  public static boolean equals(GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?> rmp, Object o) {
    if (rmp == o) return true;
    if (o == null || rmp.getClass() != o.getClass()) return false;
    GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?> that = (GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?>) o;

    IdentityHashMap<GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?>, GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?>>
        visitedMap = equalsVisited.get();

    boolean mapWasNull = visitedMap == null;
    if (mapWasNull) {
      visitedMap = new IdentityHashMap<>();
      equalsVisited.set(visitedMap);
    } else {
      if (that == visitedMap.get(rmp)) return true;
      if (visitedMap.containsKey(rmp)) return false;
    }
    visitedMap.put(rmp, that);
    boolean res = Objects.equals(rmp.fieldProjections(), that.fieldProjections());
    if (mapWasNull) equalsVisited.remove();
    return res;
  }

  public static <
      VP extends GenVarProjection<VP, ?, ?>,
      FPE extends GenFieldProjectionEntry<VP, ?, ?, FP>,
      FP extends GenFieldProjection<VP, ?, ?, FP>>
  void checkFields(@NotNull Map<String, FPE> fieldProjections, @NotNull RecordTypeApi model) {
    final Set<String> modelFieldNames = model.fieldsMap().keySet();

    for (final Map.Entry<String, FPE> entry : fieldProjections.entrySet()) {
      String fieldName = entry.getKey();

      final FieldApi field = model.fieldsMap().get(fieldName);

      if (field == null)
        throw new IllegalArgumentException(
            String.format("Field '%s' does not belong to record model '%s'. Known fields: %s",
                fieldName, model.name(), ProjectionUtils.listFields(modelFieldNames)
            )
        );

      final TypeApi projectionType = entry.getValue().fieldProjection().varProjection().type();
      if (!projectionType.isAssignableFrom(field.dataType().type()))
        throw new IllegalArgumentException(
            String.format("Field '%s' projection type '%s' is not compatible with field type '%s'",
                fieldName, projectionType.name(), field.dataType().name()
            )
        );
    }
  }

  @SuppressWarnings("unchecked") // just for IDEA, code is OK actually
  public static <
      VP extends GenVarProjection<VP, ?, ?>,
      RMP extends GenRecordModelProjection<VP, ?, ?, RMP, FPE, FP, ?>,
      FPE extends GenFieldProjectionEntry<VP, ?, ?, FP>,
      FP extends GenFieldProjection<VP, ?, ?, FP>>

  Map<FieldApi, FP> mergeFieldProjections(@NotNull List<RMP> recordProjections, final boolean keepPhantomTails) {

    Set<FieldApi> collectedFields = new LinkedHashSet<>();
    for (final RMP projection : recordProjections)
      for (final FPE entry : projection.fieldProjections().values())
        collectedFields.add(entry.field());

    Map<FieldApi, FP> mergedFields = new LinkedHashMap<>();

    List<FP> fieldProjectionsToMerge = new ArrayList<>();

    for (FieldApi field : collectedFields) {
      String fieldName = field.name();
      fieldProjectionsToMerge.clear();

      for (RMP projection : recordProjections) {
        final @Nullable FPE fieldProjectionEntry = projection.fieldProjection(fieldName);
        if (fieldProjectionEntry != null)
          fieldProjectionsToMerge.add(fieldProjectionEntry.fieldProjection());
      }

      assert !fieldProjectionsToMerge.isEmpty();
      final @NotNull FP mergedFieldProjections =
          fieldProjectionsToMerge.get(0).merge(field.dataType(), fieldProjectionsToMerge, keepPhantomTails);

      mergedFields.put(
          field,
          mergedFieldProjections
      );
    }

    return mergedFields;
  }

  @SuppressWarnings("unchecked") // just for IDEA, code is OK actually
  public static <
      VP extends GenVarProjection<VP, ?, ?>,
      RMP extends GenRecordModelProjection<VP, ?, ?, RMP, FPE, FP, ?>,
      FPE extends GenFieldProjectionEntry<VP, ?, ?, FP>,
      FP extends GenFieldProjection<VP, ?, ?, FP>>

  @NotNull Map<String, FP> normalizeFields(
      @NotNull RecordTypeApi effectiveType,
      @NotNull RMP projection,
      boolean keepPhantomTails) {

    if (projection.type().isAssignableFrom(effectiveType)) {
      Map<String, FP> result = new LinkedHashMap<>(projection.fieldProjections().size());

      for (final Map.Entry<String, FPE> entry : projection.fieldProjections().entrySet()) {
        final FieldApi effectiveField = effectiveType.fieldsMap().get(entry.getKey());
        FP fp = entry.getValue().fieldProjection();
        final VP normalizedVp =
            fp.varProjection().normalizedForType(effectiveField.dataType().type(), keepPhantomTails);
        result.put(entry.getKey(), fp.setVarProjection(normalizedVp));
      }

      return result;
    } else
      return Collections.emptyMap();
  }

  @SuppressWarnings("unchecked")
  public static <
      FPE extends GenFieldProjectionEntry<?, ?, ?, FP>,
      FP extends GenFieldProjection<?, ?, ?, FP>>
  @NotNull Map<String, FPE> reattachFields(
      @NotNull RecordTypeApi recordType,
      @NotNull Map<String, FP> fields,
      @NotNull FieldProjectionEntryFactory<FPE, FP> factory) {

    return fields.entrySet()
        .stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> factory.newFieldProjectionEntry(
                    recordType.fieldsMap().get(entry.getKey()),
                    entry.getValue(),
                    TextLocation.UNKNOWN
                ),
                (u, v) -> {
                  throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new
            )
        );
  }

  public interface FieldProjectionEntryFactory<
      FPE extends GenFieldProjectionEntry<?, ?, ?, FP>,
      FP extends GenFieldProjection<?, ?, ?, FP>> {

    @NotNull FPE newFieldProjectionEntry(
        @NotNull FieldApi fieldType,
        @NotNull FP fieldProjection,
        @NotNull TextLocation location);
  }

}
