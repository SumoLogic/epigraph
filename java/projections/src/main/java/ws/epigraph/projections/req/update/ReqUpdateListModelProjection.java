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
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.ListTypeApi;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateListModelProjection
    extends ReqUpdateModelProjection<ReqUpdateModelProjection<?, ?, ?>, ReqUpdateListModelProjection, ListTypeApi>
    implements GenListModelProjection<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?, ?>,
    ReqUpdateListModelProjection,
    ListTypeApi
    > {

  private final @NotNull ReqUpdateVarProjection itemsProjection;

  public ReqUpdateListModelProjection(
      @NotNull ListTypeApi model,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull ReqUpdateVarProjection itemsProjection,
      @NotNull TextLocation location) {
    super(model, update, params, annotations, location);
    this.itemsProjection = itemsProjection;
  }

  @Override
  public @NotNull ReqUpdateVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqUpdateListModelProjection that = (ReqUpdateListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
