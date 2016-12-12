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

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpKeyProjection {
  private final @NotNull OpParams params;
  private final @NotNull Annotations annotations;
  private final @NotNull TextLocation location;

  public OpKeyProjection(@NotNull OpParams params, @NotNull Annotations annotations, @NotNull TextLocation location) {
    this.params = params;
    this.annotations = annotations;
    this.location = location;
  }

  public @NotNull OpParams params() { return params; }

  public @NotNull Annotations annotations() { return annotations; }

  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final OpKeyProjection that = (OpKeyProjection) o;
    return Objects.equals(params, that.params) &&
           Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params, annotations);
  }
}
