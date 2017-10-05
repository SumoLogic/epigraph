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
import ws.epigraph.annotations.Annotated;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.DatumTypeApi;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractOpModelProjection<
    MP extends AbstractOpModelProjection</*MP*/?, /*SMP*/?, ?>,
    SMP extends AbstractOpModelProjection</*MP*/?, SMP, ?>,
    M extends DatumTypeApi>
    extends AbstractModelProjection<MP, SMP, M> implements Annotated {

  protected /*final*/ @NotNull Annotations annotations;
  protected /*final*/ @NotNull OpParams params;

  protected AbstractOpModelProjection(
      final @NotNull M model,
      final boolean flag,
      final @Nullable MP metaProjection,
      final @NotNull OpParams params,
      final @NotNull Annotations annotations,
      final @Nullable List<SMP> tails,
      final @NotNull TextLocation location) {
    super(model, flag, metaProjection, tails, location);
    this.annotations = annotations;
    this.params = params;
  }

  protected AbstractOpModelProjection(@NotNull M model, @NotNull TextLocation location) {
    super(model, location);
    annotations = Annotations.EMPTY;
    params = OpParams.EMPTY;
  }

  @Override
  public @NotNull Annotations annotations() { return annotations; }

  public @NotNull OpParams params() { return params; }

  @Override
  protected SMP merge(
      final @NotNull M model,
      final boolean mergedFlag,
      final @NotNull List<SMP> modelProjections,
      final MP mergedMetaProjection,
      final @Nullable List<SMP> mergedTails) {

    if (modelProjections.isEmpty()) return null;

    return merge(
        model,
        mergedFlag,
        modelProjections,
        OpParams.merge(modelProjections.stream().map(AbstractOpModelProjection::params)),
        Annotations.merge(modelProjections.stream().map(AbstractOpModelProjection::annotations)),
        mergedMetaProjection,
        mergedTails
    );
  }

  /* static */
  protected abstract SMP merge(
      @NotNull M model,
      boolean mergedFlag,
      @NotNull List<SMP> modelProjections,
      @NotNull OpParams mergedParams,
      Annotations mergedAnnotations,
      @Nullable MP mergedMetaProjection,
      @Nullable List<SMP> mergedTails);

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull SMP value) {
    preResolveCheck(value);
    this.params = value.params();
    this.annotations = value.annotations();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final AbstractOpModelProjection<?, ?, ?> that = (AbstractOpModelProjection<?, ?, ?>) o;
    return Objects.equals(params, that.params) && Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), params, annotations); }
}
