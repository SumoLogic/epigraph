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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.lang.TextLocation;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpKeyProjection extends AbstractOpKeyProjection {
  private final @NotNull AbstractOpKeyPresence presence;
  private final @NotNull TextLocation presenceLocation;

  public @NotNull OpKeyProjection(
      @NotNull AbstractOpKeyPresence presence,
      @NotNull TextLocation presenceLocation,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpModelProjection<?, ?, ?, ?> projection,
      @NotNull TextLocation location) {

    super(params, annotations, projection, location);
    this.presence = presence;
    this.presenceLocation = presenceLocation;
  }

  public AbstractOpKeyPresence presence() { return presence; }

  public @NotNull TextLocation presenceLocation() { return presenceLocation; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final OpKeyProjection that = (OpKeyProjection) o;
    return presence == that.presence;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), presence);
  }
}
