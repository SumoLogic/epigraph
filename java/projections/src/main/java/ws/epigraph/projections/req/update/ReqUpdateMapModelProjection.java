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

package ws.epigraph.projections.req.update;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenMapModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.MapTypeApi;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateMapModelProjection
    extends ReqUpdateModelProjection<ReqUpdateModelProjection<?, ?, ?>, ReqUpdateMapModelProjection, MapTypeApi>
    implements GenMapModelProjection<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?, ?>,
    ReqUpdateMapModelProjection,
    MapTypeApi
    > {

  private final boolean updateKeys;
  private final @NotNull List<ReqUpdateKeyProjection> keys;
  private final @NotNull ReqUpdateVarProjection valuesProjection;

  public ReqUpdateMapModelProjection(
      @NotNull MapTypeApi model,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      boolean updateKeys,
      @NotNull List<ReqUpdateKeyProjection> keys,
      @NotNull ReqUpdateVarProjection valuesProjection,
      @NotNull TextLocation location) {
    super(model, update, params, annotations, location);
    this.updateKeys = updateKeys;
    this.keys = keys;
    this.valuesProjection = valuesProjection;
  }

  @Override
  public @NotNull ReqUpdateVarProjection itemsProjection() { return valuesProjection; }

  public boolean updateKeys() { return updateKeys; }

  public @NotNull List<ReqUpdateKeyProjection> keys() { return keys; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final ReqUpdateMapModelProjection that = (ReqUpdateMapModelProjection) o;
    return updateKeys == that.updateKeys &&
           Objects.equals(keys, that.keys) &&
           Objects.equals(valuesProjection, that.valuesProjection);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), updateKeys, keys, valuesProjection); }
}
