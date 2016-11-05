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

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GPrimitiveDatum extends GDatum {
  @NotNull
  private final Object value;

  public GPrimitiveDatum(@Nullable TypeRef typeRef, @NotNull Object value, @NotNull TextLocation location) {
    super(typeRef, location);
    this.value = value;
  }

  @NotNull
  public Object value() { return value; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GPrimitiveDatum that = (GPrimitiveDatum) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), value); }

  @Override
  public String toString() {
    String valueString;

    if (value instanceof String) valueString = "\"" + value + '"';
    else valueString = value.toString();

    if (typeRef() == null) return valueString;
    else return typeRef() + "@" + valueString;
  }
}
