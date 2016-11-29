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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractFieldProjection;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.types.DataType;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractReqFieldProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?>,
    FP extends AbstractReqFieldProjection<VP, TP, MP, FP>
    > extends AbstractFieldProjection<VP, TP, MP, FP> {

  @NotNull
  private final ReqParams params;

  protected AbstractReqFieldProjection(
      @NotNull final ReqParams params,
      @NotNull final Annotations annotations,
      @NotNull final VP projection,
      @NotNull final TextLocation location) {
    super(annotations, projection, location);
    this.params = params;
  }

  @NotNull
  public ReqParams params() { return params; }

  @Override
  protected FP merge(
      @NotNull final DataType type,
      @NotNull final List<FP> fieldProjections,
      @NotNull final Annotations mergedAnnotations,
      @NotNull final VP mergedVarProjection) {

    return merge(
        type,
        fieldProjections,
        ReqParams.merge(fieldProjections.stream().map(AbstractReqFieldProjection::params)),
        mergedAnnotations,
        mergedVarProjection
    );
  }

  protected FP merge(
      @NotNull final DataType type,
      @NotNull final List<FP> fieldProjections,
      @NotNull final ReqParams mergedParams,
      @NotNull final Annotations mergedAnnotations,
      @NotNull final VP mergedVarProjection) {

    throw new RuntimeException("not implemented"); // todo make abstract
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final AbstractReqFieldProjection<?, ?, ?, ?> that = (AbstractReqFieldProjection<?, ?, ?, ?>) o;
    return Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), params);
  }
}
