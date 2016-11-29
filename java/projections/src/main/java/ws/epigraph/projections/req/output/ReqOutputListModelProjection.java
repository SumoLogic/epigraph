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

package ws.epigraph.projections.req.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputListModelProjection
    extends ReqOutputModelProjection<ReqOutputListModelProjection, ListType>
    implements GenListModelProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?>,
    ReqOutputListModelProjection,
    ListType
    > {

  @NotNull
  private ReqOutputVarProjection itemsProjection;

  public ReqOutputListModelProjection(
      @NotNull ListType model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputListModelProjection metaProjection,
      @NotNull ReqOutputVarProjection itemsProjection,
      @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, location);
    this.itemsProjection = itemsProjection;
  }

  @NotNull
  public ReqOutputVarProjection itemsProjection() { return itemsProjection; }

  /* static */
  @Override
  protected ReqOutputListModelProjection merge(
      @NotNull final ListType model,
      final boolean mergedRequired,
      @NotNull final List<ReqOutputListModelProjection> modelProjections,
      @NotNull final ReqParams mergedParams,
      @NotNull final Annotations mergedAnnotations,
      @Nullable final ReqOutputListModelProjection mergedMetaProjection) {

    List<ReqOutputVarProjection> itemProjections =
        modelProjections.stream()
                        .map(ReqOutputListModelProjection::itemsProjection)
                        .collect(Collectors.toList());

    @NotNull final ReqOutputVarProjection mergedItemsVarType = itemProjections.get(0).merge(itemProjections);

    return new ReqOutputListModelProjection(
        model,
        mergedRequired,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedItemsVarType,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ReqOutputListModelProjection that = (ReqOutputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
