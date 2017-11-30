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
import ws.epigraph.projections.NormalizationContext;
import ws.epigraph.projections.abs.AbstractModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.*;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReqModelProjection<
    MP extends ReqModelProjection</*MP*/?, /*SMP*/? extends MP, /*M*/?>,
    SMP extends ReqModelProjection</*MP*/?, SMP, ?>,
    M extends DatumTypeApi>
    extends AbstractModelProjection<ReqEntityProjection, ReqTagProjectionEntry, MP, SMP, M>
    implements ReqProjection<SMP, MP> {

  protected /*final*/ @NotNull Directives directives;
  protected /*final*/ @NotNull ReqParams params;

  protected ReqModelProjection(
      final @NotNull M model,
      final boolean flag,
      final @NotNull ReqParams params,
      final @NotNull Directives directives,
      final @Nullable MP metaProjection,
      final @Nullable List<SMP> tails,
      final @NotNull TextLocation location
  ) {
    super(
        model,
        flag,
        metaProjection,
        tails,
        location,
        self -> new ReqTagProjectionEntry(model.self(), self, location)
    );
    this.directives = directives;
    this.params = params;
  }

  protected ReqModelProjection(final @NotNull M model, final @NotNull TextLocation location) {
    super(model, location);
    params = ReqParams.EMPTY;
    directives = Directives.EMPTY;
  }

  public @NotNull Directives directives() { return directives; }

  public @NotNull ReqParams params() { return params; }

  @SuppressWarnings("unchecked")
  @Override
  protected @NotNull NormalizationContext<TypeApi, SMP> newNormalizationContext() {
    return new NormalizationContext<>(m -> {
      switch (m.kind()) {
        case RECORD:
          return (SMP) new ReqRecordModelProjection((RecordTypeApi) m, TextLocation.UNKNOWN);
        case MAP:
          return (SMP) new ReqMapModelProjection((MapTypeApi) m, TextLocation.UNKNOWN);
        case LIST:
          return (SMP) new ReqListModelProjection((ListTypeApi) m, TextLocation.UNKNOWN);
        case PRIMITIVE:
          return (SMP) new ReqPrimitiveModelProjection((PrimitiveTypeApi) m, TextLocation.UNKNOWN);
        default:
          throw new IllegalArgumentException("Unsupported model kind: " + m.kind());
      }
    });
  }

  @Override
  protected SMP merge(
      final @NotNull M model,
      final boolean mergedFlag,
      final @NotNull List<SMP> modelProjections,
      final @Nullable MP mergedMetaProjection,
      final @Nullable List<SMP> mergedTails) {

    return merge(
        model,
        mergedFlag,
        modelProjections,
        ReqParams.merge(modelProjections.stream().map(ReqModelProjection::params)),
        Directives.merge(modelProjections.stream().map(ReqModelProjection::directives)),
        mergedMetaProjection,
        mergedTails
    );
  }

  protected abstract SMP merge(
      @NotNull M model,
      boolean mergedFlag,
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
    final ReqModelProjection<?, ?, ?> that = (ReqModelProjection<?, ?, ?>) o;
    return Objects.equals(params, that.params) && Objects.equals(directives, that.directives);
  }

  @Override
  public int hashCode() { return Objects.hash(super.hashCode(), params, directives); }
}
