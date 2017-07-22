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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GListDatum extends GDatum {
  private final @NotNull List<GDataValue> values;

  public GListDatum(@Nullable TypeRef typeRef, @NotNull List<GDataValue> values, @NotNull TextLocation location) {
    super(typeRef, location);
    this.values = values;
  }

  public @NotNull List<GDataValue> values() { return values; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GListDatum gListDatum = (GListDatum) o;
    return Objects.equals(values, gListDatum.values);
  }

  void foo() {
    LinkedHashMap<String, Long> map =
        Stream.<AbstractMap.Entry<String, Long>>of(
            new AbstractMap.SimpleEntry<>("", 1L),
            new AbstractMap.SimpleEntry<>("", 1L)
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));

  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), values); }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (typeRef() != null) sb.append(typeRef());
    sb.append('[');
    sb.append(values.stream().map(Object::toString).collect(Collectors.joining(", ")));
    sb.append(']');
    return sb.toString();
  }
}
