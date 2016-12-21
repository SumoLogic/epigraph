/*
 * Copyright 2016 Sumo Logic
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

import ws.epigraph.gdata.GDataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Annotations {
  public static final Annotations EMPTY = new Annotations(Collections.emptyMap());

  private final @NotNull Map<String, Annotation> entries;

  public static @NotNull Annotations fromMap(@Nullable Map<String, Annotation> entries) {
    return entries == null ? EMPTY : new Annotations(entries);
  }

  public Annotations(@NotNull Map<String, Annotation> entries) {this.entries = entries;}

  public Annotations(@NotNull Collection<Annotation> annotations) {
    this(annotations.stream().collect(Collectors.toMap(Annotation::name, Function.identity())));
  }

  public boolean isEmpty() { return entries.isEmpty(); }

  public @Nullable GDataValue get(@NotNull String key) {
    Annotation annotation = entries.get(key);
    return annotation == null ? null : annotation.value();
  }

  public @NotNull Map<String, Annotation> asMap() { return entries; }

  public static @NotNull Annotations merge(@NotNull Stream<Annotations> annotationsToMerge) {
    Map<String, Annotation> entries = new HashMap<>();

    annotationsToMerge.forEach(annotations -> {
      for (final Map.Entry<String, Annotation> entry : annotations.asMap().entrySet()) {
        String key = entry.getKey();
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
