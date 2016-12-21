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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GEnumDatum extends GDatum {
  private final @NotNull String value;

  public GEnumDatum(@NotNull String value, @NotNull TextLocation location) {
    super(null, location);
    this.value = value;
  }

  public @NotNull String value() { return value; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GEnumDatum gEnumDatum = (GEnumDatum) o;
    return Objects.equals(value, gEnumDatum.value);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), value); }

  @Override
  public String toString() { return "#" + value; }
}
