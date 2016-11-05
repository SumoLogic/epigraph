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

package ws.epigraph.gdata;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.refs.TypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GListDatum extends GDatum {
  @NotNull
  private final List<GDataValue> values;

  public GListDatum(@Nullable TypeRef typeRef, @NotNull List<GDataValue> values, @NotNull TextLocation location) {
    super(typeRef, location);
    this.values = values;
  }

  @NotNull
  public List<GDataValue> values() { return values; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GListDatum gListDatum = (GListDatum) o;
    return Objects.equals(values, gListDatum.values);
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
