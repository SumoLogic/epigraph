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

package ws.epigraph.gdata;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.refs.TypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GData extends GDataValue {
  private final @Nullable TypeRef typeRef;
  private final @NotNull LinkedHashMap<String, GDatum> tags;

  public GData(
      @Nullable TypeRef typeRef,
      @NotNull LinkedHashMap<String, GDatum> tags,
      @NotNull TextLocation location) {

    super(location);
    this.typeRef = typeRef;
    this.tags = tags;
  }

  public @Nullable TypeRef typeRef() { return typeRef; }

  public @NotNull LinkedHashMap<String, GDatum> tags() { return tags; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GData gData = (GData) o;
    return Objects.equals(typeRef, gData.typeRef) &&
           Objects.equals(tags, gData.tags);
  }

  @Override
  public int hashCode() { return Objects.hash(typeRef, tags); }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (typeRef() != null) sb.append(typeRef());
    sb.append('<');
    sb.append(tags.entrySet()
        .stream()
        .map(e -> e.getKey() + ": " + e.getValue())
        .collect(Collectors.joining(", ")));
    sb.append('>');
    return sb.toString();
  }
}
