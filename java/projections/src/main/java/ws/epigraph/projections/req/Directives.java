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

package ws.epigraph.projections.req;

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
public class Directives {
  public static final Directives EMPTY = new Directives(Collections.emptyMap());

  private final @NotNull Map<String, Directive> entries;

  public static @NotNull Directives fromMap(@Nullable Map<String, Directive> entries) {
    return entries == null ? EMPTY : new Directives(entries);
  }

  public Directives(@NotNull Map<String, Directive> entries) {this.entries = entries;}

  public Directives(@NotNull Collection<Directive> directives) {
    this(directives.stream().collect(Collectors.toMap(Directive::name, Function.identity())));
  }

  public boolean isEmpty() { return entries.isEmpty(); }

  public @Nullable GDataValue get(@NotNull String key) {
    Directive directive = entries.get(key);
    return directive == null ? null : directive.value();
  }

  public @NotNull Map<String, Directive> asMap() { return entries; }

  public static @NotNull Directives merge(@NotNull Stream<Directives> directivessToMerge) {
    Map<String, Directive> entries = new HashMap<>();

    directivessToMerge.forEach(directives -> {
      for (final Map.Entry<String, Directive> entry : directives.asMap().entrySet()) {
        String key = entry.getKey();
        if (!entries.containsKey(key))
          entries.put(key, entry.getValue());
      }
    });

    return new Directives(entries);
  }

  public static @NotNull Directives merge(@NotNull Collection<Directives> directivesToMerge) {
    if (directivesToMerge.isEmpty()) return EMPTY;
    if (directivesToMerge.size() == 1) return directivesToMerge.iterator().next();

    return merge(directivesToMerge.stream());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Directives opParams = (Directives) o;
    return Objects.equals(entries, opParams.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entries);
  }
}
