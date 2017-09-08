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
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.req.AbstractReqFieldProjection;
import ws.epigraph.types.DataTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputFieldProjection extends AbstractReqFieldProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>,
    ReqOutputFieldProjection
    > {

//  private final boolean flagged;

  public ReqOutputFieldProjection(
//      @NotNull ReqParams reqParams,
//      @NotNull Annotations annotations,
      @NotNull ReqOutputVarProjection projection,
//      boolean flagged,
      @NotNull TextLocation location) {
    super(/*reqParams, annotations, */projection, location);
//    this.flagged = flagged;
  }

  /**
   * @return {@code true} iff field entity projection is flagged
   */
  public boolean flagged() {
    return flagged(varProjection());
  }

  private static boolean flagged(@NotNull ReqOutputVarProjection vp) {
    return vp.flagged();
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
//        fieldProjections.stream().anyMatch(ReqOutputFieldProjection::flagged),
        TextLocation.UNKNOWN
    );
  }

//  @Override
//  public boolean equals(final Object o) {
//    if (this == o) return true;
//    if (o == null || getClass() != o.getClass()) return false;
//    if (!super.equals(o)) return false;
//    final ReqOutputFieldProjection that = (ReqOutputFieldProjection) o;
//    return flagged == that.flagged;
//  }
//
//  @Override
//  public int hashCode() {
//    return Objects.hash(super.hashCode(), flagged);
//  }
}
