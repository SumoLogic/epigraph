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
import ws.epigraph.projections.abs.AbstractFieldProjection;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.types.DataTypeApi;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractOpFieldProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?, ?>,
    FP extends AbstractOpFieldProjection<VP, TP, MP, FP>
    > extends AbstractFieldProjection<VP, TP, MP, FP> {

//  private final @NotNull OpParams params;

  protected AbstractOpFieldProjection(
//      @NotNull OpParams params,
//      final @NotNull Annotations annotations,
      final @NotNull VP projection,
      final @NotNull TextLocation location) {
    super(/* annotations, */ projection, location);
//    this.params = params;
  }

//  public @NotNull OpParams params() { return params; }

//  @Override
//  protected FP merge(
//      final @NotNull DataTypeApi type,
//      final @NotNull List<FP> fieldProjections,
//      final @NotNull Annotations mergedAnnotations,
//      final @NotNull VP mergedVarProjection) {
//
//    return merge(
//        type,
//        fieldProjections,
//        OpParams.merge(fieldProjections.stream().map(AbstractOpFieldProjection::params)),
//        mergedAnnotations,
//        mergedVarProjection
//    );
//  }
//
//  protected @NotNull FP merge(
//      final @NotNull DataTypeApi type,
//      final @NotNull List<FP> fieldProjections,
//      final @NotNull OpParams mergedParams,
//      final @NotNull Annotations mergedAnnotations,
//      final @NotNull VP mergedVarProjection) {
//
//    throw new RuntimeException("not implemented"); // todo make abstract
//  }

//  @Override
//  public boolean equals(final Object o) {
//    if (this == o) return true;
//    if (o == null || getClass() != o.getClass()) return false;
//    if (!super.equals(o)) return false;
//    final AbstractOpFieldProjection<?, ?, ?, ?> that = (AbstractOpFieldProjection<?, ?, ?, ?>) o;
//    return Objects.equals(params, that.params);
//  }
//
//  @Override
//  public int hashCode() {
//    return Objects.hash(super.hashCode(), params);
//  }
}
