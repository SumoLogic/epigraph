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

package ws.epigraph.annotations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.lang.Qn;
import ws.epigraph.names.QualifiedTypeName;
import ws.epigraph.names.TypeName;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.DatumTypeApi;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Annotations {
  public static final Annotations EMPTY = new Annotations(Collections.emptyMap());

  private final @NotNull Map<Qn, Annotation> entries;

  public static @NotNull Annotations fromMap(@Nullable Map<DatumTypeApi, Annotation> entries) {
    return entries == null ? EMPTY : new Annotations(entries);
  }

  public Annotations(@NotNull Map<DatumTypeApi, Annotation> entries) {
    this.entries = new HashMap<>();
    for (final Map.Entry<DatumTypeApi, Annotation> entry : entries.entrySet()) {
      DatumTypeApi type = entry.getKey();
      TypeName typeName = type.name();
      Annotation annotation = entry.getValue();

      if (typeName instanceof QualifiedTypeName) {
        QualifiedTypeName qualifiedTypeName = (QualifiedTypeName) typeName;
        this.entries.put(qualifiedTypeName.toFqn(), annotation);
      } else
        throw new IllegalArgumentException(
            String.format("Annotations can only be based on qualified types (type: %s, annotation location: %s)",
                typeName, annotation.location()
            ));
    }
  }

  public Annotations(@NotNull Collection<Annotation> annotations) {
    this(annotations.stream().collect(Collectors.toMap(Annotation::type, Function.identity())));
  }

  public boolean isEmpty() { return entries.isEmpty(); }

  public @Nullable Annotation annotation(@NotNull DatumTypeApi type) {
    TypeName typeName = type.name();
    if (typeName instanceof QualifiedTypeName) {
      QualifiedTypeName qualifiedTypeName = (QualifiedTypeName) typeName;
      return entries.get(qualifiedTypeName.toFqn());
    } else return null;
  }

  public @Nullable Annotation annotation(@NotNull Qn typeName) { return entries.get(typeName); }

  /**
   * @return annotation value or {@code null} if such annotation doesn't exist
   * @throws IllegalStateException    if called during compile-time (and data types are not available)
   * @throws IllegalArgumentException if {@code GDatum} value provided for this annotation cannot be converted to given type
   */
  public @Nullable Datum getDatum(@NotNull DatumTypeApi type) throws IllegalStateException, IllegalArgumentException {
    Annotation annotation = annotation(type);
    return annotation == null ? null : annotation.value();
  }

  /**
   * @return annotation value or {@code null} if such annotation doesn't exist
   * @throws IllegalStateException    if called during compile-time (and data types are not available)
   * @throws IllegalArgumentException if {@code GDatum} value provided for this annotation cannot be converted to given type
   */
  @SuppressWarnings("unchecked")
  public <D extends Datum.Imm.Static> @Nullable D get(@NotNull DatumType.Static<D, ?, ?, ?, ?, ?> type) {
    Datum datum = getDatum(type);
    return (D) datum;
  }

  public @Nullable GDatum getGDatum(@NotNull Qn typeName) {
    Annotation annotation = annotation(typeName);
    return annotation == null ? null : annotation.gDatum();
  }

  public @NotNull Map<DatumTypeApi, Annotation> asMap() {
    return entries.values().stream().collect(Collectors.toMap(Annotation::type, Function.identity()));
  }

  public static @NotNull Annotations merge(@NotNull Stream<Annotations> annotationsToMerge) {
    Map<DatumTypeApi, Annotation> entries = new HashMap<>();

    annotationsToMerge.forEach(annotations -> {
      for (final Map.Entry<DatumTypeApi, Annotation> entry : annotations.asMap().entrySet()) {
        DatumTypeApi key = entry.getKey();
        if (!entries.containsKey(key))
          entries.put(key, entry.getValue());
      }
    });

    return new Annotations(entries);
  }

  public static @NotNull Annotations merge(@NotNull Collection<Annotations> annotationsToMerge) {
    if (annotationsToMerge.isEmpty()) return EMPTY;
    if (annotationsToMerge.size() == 1) return annotationsToMerge.iterator().next();

    return merge(annotationsToMerge.stream());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Annotations opParams = (Annotations) o;
    return Objects.equals(entries, opParams.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entries);
  }
}
