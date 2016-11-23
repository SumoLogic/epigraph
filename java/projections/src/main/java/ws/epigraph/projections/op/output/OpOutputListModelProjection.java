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

package ws.epigraph.projections.op.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenListModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.ListType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputListModelProjection
    extends OpOutputModelProjection<OpOutputListModelProjection, ListType>
    implements GenListModelProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?>,
    OpOutputListModelProjection,
    ListType
    > {

  @NotNull
  private OpOutputVarProjection itemsProjection;

  public OpOutputListModelProjection(
      @NotNull ListType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputListModelProjection metaProjection,
      @NotNull OpOutputVarProjection itemsProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
    this.itemsProjection = itemsProjection;
  }

  @NotNull
  public OpOutputVarProjection itemsProjection() { return itemsProjection; }

  /* static */
  @Override
  protected OpOutputListModelProjection merge(
      @NotNull final ListType model,
      @NotNull final List<OpOutputListModelProjection> modelProjections,
      @NotNull final OpParams mergedParams,
      @NotNull final Annotations mergedAnnotations,
      @Nullable final OpOutputListModelProjection mergedMetaProjection) {

    List<OpOutputVarProjection> itemProjections =
        modelProjections.stream()
                        .map(OpOutputListModelProjection::itemsProjection)
                        .collect(Collectors.toList());

    @NotNull final OpOutputVarProjection mergedItemsVarType = itemProjections.get(0).merge(itemProjections);

    return new OpOutputListModelProjection(
        model,
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
    OpOutputListModelProjection that = (OpOutputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }
}
