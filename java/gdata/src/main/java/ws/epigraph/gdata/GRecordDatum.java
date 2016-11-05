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

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GRecordDatum extends GDatum {
  @NotNull
  private final LinkedHashMap<String, GDataValue> fields;

  public GRecordDatum(@Nullable TypeRef typeRef,
                      @NotNull LinkedHashMap<String, GDataValue> fields,
                      @NotNull TextLocation location) {

    super(typeRef, location);
    this.fields = fields;
  }

  @NotNull
  public LinkedHashMap<String, GDataValue> fields() { return fields; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GRecordDatum gDataMap = (GRecordDatum) o;
    return Objects.equals(fields, gDataMap.fields);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), fields); }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (typeRef() != null) sb.append(typeRef());
    sb.append('{');
    sb.append(fields.entrySet()
                    .stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(Collectors.joining(", ")));
    sb.append('}');
    return sb.toString();
  }
}
