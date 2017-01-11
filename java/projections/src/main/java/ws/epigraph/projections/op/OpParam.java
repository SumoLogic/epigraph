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

package ws.epigraph.projections.op;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpParam {
  private final @NotNull String name;
  private final @NotNull OpInputModelProjection<?, ?, ?, ?> projection;
  private final @NotNull TextLocation location;

  public OpParam(@NotNull String name,
                 @NotNull OpInputModelProjection<?, ?, ?, ?> projection,
                 @NotNull TextLocation location) {
    this.name = name;
    this.projection = projection;
    this.location = location;
  }

  // note: annotations are on the model projection

  public @NotNull String name() { return name; }

  public @NotNull OpInputModelProjection<?, ?, ?, ?> projection() { return projection; }

  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpParam opParam = (OpParam) o;
    return Objects.equals(name, opParam.name) &&
           Objects.equals(projection, opParam.projection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, projection);
  }
}
