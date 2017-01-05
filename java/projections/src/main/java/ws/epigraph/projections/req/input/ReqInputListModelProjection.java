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

package ws.epigraph.projections.req.input;

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
public class ReqInputListModelProjection
    extends ReqInputModelProjection<ReqInputListModelProjection, ListTypeApi>
    implements GenListModelProjection<
    ReqInputVarProjection,
    ReqInputTagProjectionEntry,
    ReqInputModelProjection<?, ?>,
    ReqInputListModelProjection,
    ListTypeApi
    > {

  private final @NotNull ReqInputVarProjection itemsProjection;

  public ReqInputListModelProjection(
      @NotNull ListTypeApi model,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull ReqInputVarProjection itemsProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);
    this.itemsProjection = itemsProjection;
  }

  @Override
  public @NotNull ReqInputVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqInputListModelProjection that = (ReqInputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
