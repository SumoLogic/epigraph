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
import ws.epigraph.lang.TextLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Annotation {
  @NotNull
  private final String name;
  @NotNull
  private final GDataValue value;
  @NotNull
  private final TextLocation location;

  public Annotation(@NotNull String name, @NotNull GDataValue value, @NotNull TextLocation location) {
    this.name = name;
    this.value = value;
    this.location = location;
  }

  @NotNull
  public String name() { return name; }

  @NotNull
  public GDataValue value() { return value; }

  @NotNull
  public TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Annotation that = (Annotation) o;
    return Objects.equals(name, that.name) &&
           Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }
}
