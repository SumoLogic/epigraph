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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractModelProjection;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.types.DatumTypeApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractOpModelProjection<
    MP extends AbstractOpModelProjection</*MP*/?, /*SMP*/?, ?>,
    SMP extends AbstractOpModelProjection</*MP*/?, /*SMP*/?, ?>,
    M extends DatumTypeApi>
    extends AbstractModelProjection<MP, SMP, M> {
  protected final @NotNull OpParams params;

  protected AbstractOpModelProjection(
      final @NotNull M model,
      final @Nullable MP metaProjection,
      final @NotNull OpParams params,
      final @NotNull Annotations annotations,
      final @NotNull TextLocation location) {
    super(model, metaProjection, annotations, location);
    this.params = params;
  }

  public @NotNull OpParams params() { return params; }

  @Override
  protected SMP merge(
      final @NotNull M model,
      final @NotNull List<SMP> modelProjections,
      final @NotNull Annotations mergedAnnotations,
      final MP mergedMetaProjection) {

    if (modelProjections.isEmpty()) return null;

    Collection<OpParams> paramsList = new ArrayList<>();

    for (final GenModelProjection<?, ?, ?> projection : modelProjections) {
      AbstractOpModelProjection<?, ?, ?> mp = (AbstractOpModelProjection<?, ?, ?>) projection;
      paramsList.add(mp.params());
    }

    return merge(
        model,
        modelProjections, OpParams.merge(paramsList), mergedAnnotations, mergedMetaProjection
    );
  }

  /* static */
  protected SMP merge(
      @NotNull M model,
      @NotNull List<SMP> modelProjections,
      @NotNull OpParams mergedParams,
      @NotNull Annotations mergedAnnotations,
      @Nullable MP mergedMetaProjection) {

    throw new RuntimeException("not implemented"); // todo
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final AbstractOpModelProjection<?, ?, ?> that = (AbstractOpModelProjection<?, ?, ?>) o;
    return Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), params);
  }
}
