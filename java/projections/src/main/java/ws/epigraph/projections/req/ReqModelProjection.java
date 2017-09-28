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
import ws.epigraph.projections.abs.AbstractVarProjection;
import ws.epigraph.projections.ModelNormalizationContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.*;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReqModelProjection<
    MP extends ReqModelProjection</*MP*/?, /*SMP*/?, ?>,
    SMP extends ReqModelProjection</*MP*/?, SMP, ?>,
    M extends DatumTypeApi>
    extends AbstractReqModelProjection<MP, SMP, M> {

  protected /*final*/ boolean flagged;

  protected ReqModelProjection(
      @NotNull M model,
      boolean flagged,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable MP metaProjection,
      @Nullable List<SMP> tails,
      @NotNull TextLocation location) {
    super(model, params, metaProjection, directives, tails, location);
    this.flagged = flagged;
  }

  protected ReqModelProjection(final @NotNull M model, final @NotNull TextLocation location) {
    super(model, location);
  }

  public boolean flagged() {
    assert isResolved();
    return flagged;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected @NotNull ModelNormalizationContext<M, SMP> newNormalizationContext() {
    return new ModelNormalizationContext<>(m -> {
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
  public SMP setEntityProjection(final @NotNull AbstractVarProjection<?, ?, ?> entityProjection) {
    SMP res = super.setEntityProjection(entityProjection);
    if (entityProjection instanceof ReqEntityProjection) {
      ReqEntityProjection r = (ReqEntityProjection) entityProjection;
      if (r.flagged())
        res.flagged = true;
      else if (flagged())
        r.flagged = true;
    }
    return res;
  }

  @Override
  protected SMP merge(
      final @NotNull M model,
      final @NotNull List<SMP> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable MP mergedMetaProjection,
      final @Nullable List<SMP> mergedTails) {

    return merge(
        model,
        modelProjections.stream().anyMatch(ReqModelProjection::flagged),
        modelProjections,
        mergedParams,
        mergedDirectives,
        mergedMetaProjection,
        mergedTails
    );
  }

  protected abstract SMP merge(
      @NotNull M model,
      boolean mergedFlagged,
      @NotNull List<SMP> modelProjections,
      @NotNull ReqParams mergedParams,
      @NotNull Directives mergedDirectives,
      @Nullable MP mergedMetaProjection,
      @Nullable List<SMP> mergedTails);

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull SMP value) {
    preResolveCheck(value);
    flagged = value.flagged();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final ReqModelProjection<?, ?, ?> that = (ReqModelProjection<?, ?, ?>) o;
    return flagged == that.flagged;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), flagged);
  }
}
