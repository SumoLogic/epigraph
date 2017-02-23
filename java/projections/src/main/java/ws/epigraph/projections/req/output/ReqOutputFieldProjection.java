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

package ws.epigraph.projections.req.output;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.req.AbstractReqFieldProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.DataTypeApi;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputFieldProjection extends AbstractReqFieldProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>,
    ReqOutputFieldProjection
    > {

//  private final boolean required;

  public ReqOutputFieldProjection(
//      @NotNull ReqParams reqParams,
//      @NotNull Annotations annotations,
      @NotNull ReqOutputVarProjection projection,
//      boolean required,
      @NotNull TextLocation location) {
    super(/*reqParams, annotations, */projection, location);
//    this.required = required;
  }

  /**
   * @return {@code true} iff all models (including polymorphic tails) are required
   */
  public boolean required() {
    return required(varProjection());
  }

  private static boolean required(@NotNull ReqOutputVarProjection vp) {
    if (vp.tagProjections().isEmpty() && vp.polymorphicTails() == null) return false;

    if (vp.tagProjections().values().stream().anyMatch(tpe -> !tpe.projection().required()))
      return false;
    final Collection<ReqOutputVarProjection> tails = vp.polymorphicTails();
    return tails == null || tails.stream().allMatch(ReqOutputFieldProjection::required);
  }

  @Override
  public @NotNull ReqOutputFieldProjection setVarProjection(final @NotNull ReqOutputVarProjection varProjection) {
    return new ReqOutputFieldProjection(varProjection, TextLocation.UNKNOWN);
  }

  @Override
  protected ReqOutputFieldProjection merge(
      final @NotNull DataTypeApi type,
      final @NotNull List<ReqOutputFieldProjection> fieldProjections,
//      final @NotNull ReqParams mergedParams,
//      final @NotNull Annotations mergedAnnotations,
      final @NotNull ReqOutputVarProjection mergedVarProjection) {

    return new ReqOutputFieldProjection(
//        mergedParams,
//        mergedAnnotations,
        mergedVarProjection,
//        fieldProjections.stream().anyMatch(ReqOutputFieldProjection::required),
        TextLocation.UNKNOWN
    );
  }

//  @Override
//  public boolean equals(final Object o) {
//    if (this == o) return true;
//    if (o == null || getClass() != o.getClass()) return false;
//    if (!super.equals(o)) return false;
//    final ReqOutputFieldProjection that = (ReqOutputFieldProjection) o;
//    return required == that.required;
//  }
//
//  @Override
//  public int hashCode() {
//    return Objects.hash(super.hashCode(), required);
//  }
}
