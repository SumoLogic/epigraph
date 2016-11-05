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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Annotations {
  public static final Annotations EMPTY = new Annotations(Collections.emptyMap());

  @NotNull
  private final Map<String, Annotation> entries;

  @NotNull
  public static Annotations fromMap(@Nullable Map<String, Annotation> entries) {
    return entries == null ? EMPTY : new Annotations(entries);
  }

  public Annotations(@NotNull Map<String, Annotation> entries) {this.entries = entries;}

  public Annotations(@NotNull Collection<Annotation> annotations) {
    this(annotations.stream().collect(Collectors.toMap(Annotation::name, Function.identity())));
  }

  public boolean hasParam(@NotNull String name) { return entries.containsKey(name); }

  public boolean isEmpty() { return entries.isEmpty(); }

  @Nullable
  public GDataValue get(@NotNull String key) {
    Annotation annotation = entries.get(key);
    return annotation == null ? null : annotation.value();
  }

  @NotNull
  public Map<String, Annotation> params() { return entries; }

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
