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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
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

  protected /*final*/ @NotNull Directives directives;
  protected /*final*/ @NotNull ReqParams params;

  protected AbstractReqModelProjection(
      final @NotNull M model,
      final @NotNull ReqParams params,
      final @Nullable MP metaProjection,
      final @NotNull Directives directives,
      final @Nullable List<SMP> tails,
      final @NotNull TextLocation location
  ) {
    super(model, metaProjection, tails, location);
    this.directives = directives;
    this.params = params;
  }

  protected AbstractReqModelProjection(final @NotNull M model, final @NotNull TextLocation location) {
    super(model, location);
    params = ReqParams.EMPTY;
    directives = Directives.EMPTY;
  }

  public @NotNull Directives directives() { return directives; }

  public @NotNull ReqParams params() { return params; }

  @Override
  protected SMP merge(
      final @NotNull M model,
      final @NotNull List<SMP> modelProjections,
      final @Nullable MP mergedMetaProjection,
      final @Nullable List<SMP> mergedTails) {


    return merge(
        model,
        modelProjections,
        ReqParams.merge(modelProjections.stream().map(AbstractReqModelProjection::params)),
        Directives.merge(modelProjections.stream().map(AbstractReqModelProjection::directives)),
        mergedMetaProjection,
        mergedTails
    );
  }

  protected abstract SMP merge(
      @NotNull M model,
      @NotNull List<SMP> modelProjections,
      @NotNull ReqParams mergedParams,
      @NotNull Directives mergedDirectives,
      @Nullable MP mergedMetaProjection,
      @Nullable List<SMP> mergedTails);

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull SMP value) {
    preResolveCheck(value);
    params = value.params();
    directives = value.directives();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final AbstractReqModelProjection<?, ?, ?> that = (AbstractReqModelProjection<?, ?, ?>) o;
    return Objects.equals(params, that.params) && Objects.equals(directives, that.directives);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), params, directives); }
}
