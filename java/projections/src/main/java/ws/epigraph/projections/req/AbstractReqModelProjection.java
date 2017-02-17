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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractModelProjection;
import ws.epigraph.types.DatumTypeApi;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractReqModelProjection<
    MP extends AbstractReqModelProjection</*MP*/?, /*SMP*/?, ?>,
    SMP extends AbstractReqModelProjection</*MP*/?, SMP, ?>,
    M extends DatumTypeApi>
    extends AbstractModelProjection<MP, SMP, M> {

  protected final @NotNull ReqParams params;

  protected AbstractReqModelProjection(
      final @NotNull M model,
      final @NotNull ReqParams params,
      final @Nullable MP metaProjection,
      final @NotNull Annotations annotations,
      final @Nullable List<SMP> tails,
      final @NotNull TextLocation location
  ) {
    super(model, metaProjection, annotations, tails, location);
    this.params = params;
  }

  public @NotNull ReqParams params() { return params; }

  @Override
  protected SMP merge(
      final @NotNull M model,
      final @NotNull List<SMP> modelProjections,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable MP mergedMetaProjection,
      final @Nullable List<SMP> mergedTails) {

    return merge(
        model,
        modelProjections,
        ReqParams.merge(modelProjections.stream().map(AbstractReqModelProjection::params)),
        mergedAnnotations,
        mergedMetaProjection,
        mergedTails
    );
  }

  protected SMP merge(
      final @NotNull M model,
      final @NotNull List<SMP> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable MP mergedMetaProjection,
      final @Nullable List<SMP> mergedTails) {
    throw new RuntimeException("not implemented"); // todo make abstract
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final AbstractReqModelProjection<?, ?, ?> that = (AbstractReqModelProjection<?, ?, ?>) o;
    return Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), params);
  }
}
