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

package ws.epigraph.projections.op.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpKeyProjection;
import ws.epigraph.projections.op.OpParams;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputKeyProjection extends OpKeyProjection {
  private final @NotNull OpKeyPresence presence;

  public @NotNull OpOutputKeyProjection(
      @NotNull OpKeyPresence presence,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?, ?> projection,
      @NotNull TextLocation location) {

    super(params, annotations, projection, location);
    this.presence = presence;
  }

  public OpKeyPresence presence() { return presence; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final OpOutputKeyProjection that = (OpOutputKeyProjection) o;
    return presence == that.presence;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), presence);
  }
}
