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

package ws.epigraph.projections.req.update;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.AbstractReqModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.DatumTypeApi;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReqUpdateModelProjection<
    MP extends ReqUpdateModelProjection</*MP*/?, /*SMP*/?, ?>,
    SMP extends ReqUpdateModelProjection</*MP*/?, SMP, ?>,
    M extends DatumTypeApi>
    extends AbstractReqModelProjection<MP, SMP, M> {

  protected /*final*/ boolean update;

  protected ReqUpdateModelProjection(
      @NotNull M model,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable List<SMP> tails,
      @NotNull TextLocation location
  ) {
    super(model, params, null, annotations, tails, location);
    this.update = update;
  }

  protected ReqUpdateModelProjection(final @NotNull M model, final @NotNull TextLocation location) {
    super(model, location);
  }

  /**
   * @return {@code true} if this model must be updated (replaced), {@code false} if it must be patched
   */
  public boolean update() {
    assert isResolved();
    return update;
  }

  @Override
  public void resolve(final ProjectionReferenceName name, final @NotNull SMP value) {
    super.resolve(name, value);
    update = value.update();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final ReqUpdateModelProjection<?, ?, ?> that = (ReqUpdateModelProjection<?, ?, ?>) o;
    return update == that.update;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), update);
  }
}
