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

package ws.epigraph.projections.req.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.req.AbstractReqModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.DatumTypeApi;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReqOutputModelProjection<
    MP extends ReqOutputModelProjection</*MP*/?, /*SMP*/?, ?>,
    SMP extends ReqOutputModelProjection</*MP*/?, SMP, ?>,
    M extends DatumTypeApi>
    extends AbstractReqModelProjection<MP, SMP, M> {

  protected /*final*/ boolean required;

  protected ReqOutputModelProjection(
      @NotNull M model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable MP metaProjection,
      @Nullable List<SMP> tails,
      @NotNull TextLocation location) {
    super(model, params, metaProjection, annotations, tails, location);
    this.required = required;
  }

  protected ReqOutputModelProjection(final @NotNull M model, final @NotNull TextLocation location) {
    super(model, location);
  }

  public boolean required() {
    assert isResolved();
    return required;
  }

  @Override
  protected SMP merge(
      final @NotNull M model,
      final @NotNull List<SMP> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable MP mergedMetaProjection,
      final @Nullable List<SMP> mergedTails) {

    return merge(
        model,
        modelProjections.stream().anyMatch(ReqOutputModelProjection::required),
        modelProjections,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedTails
    );
  }

  protected SMP merge(
      final @NotNull M model,
      final boolean mergedRequired,
      final @NotNull List<SMP> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable MP mergedMetaProjection,
      final @Nullable List<SMP> mergedTails) {

    throw new RuntimeException("not implemented"); // todo make abstract
  }

  @Override
  public void resolve(final @Nullable Qn name, final @NotNull SMP value) {
    super.resolve(name, value);
    required = value.required();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final ReqOutputModelProjection<?, ?, ?> that = (ReqOutputModelProjection<?, ?, ?>) o;
    return required == that.required;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), required);
  }
}
