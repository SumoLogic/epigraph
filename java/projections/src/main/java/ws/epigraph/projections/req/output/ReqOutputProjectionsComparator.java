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
import ws.epigraph.projections.req.ReqProjectionsComparator;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsComparator extends ReqProjectionsComparator<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>,
    ReqOutputRecordModelProjection,
    ReqOutputMapModelProjection,
    ReqOutputListModelProjection,
    ReqOutputPrimitiveModelProjection,
    ReqOutputFieldProjectionEntry,
    ReqOutputFieldProjection
    > {

  public ReqOutputProjectionsComparator(final boolean compareAnnotations, final boolean compareParams) {
    super(compareAnnotations, compareParams);
  }

  @Override
  protected boolean mapModelEquals(
      final @NotNull Collection<@NotNull ReqOutputMapModelProjection> mps1,
      final @NotNull Collection<@NotNull ReqOutputMapModelProjection> mps2) {

    Set<ReqOutputKeyProjection> keys1 = mps1.stream()
        .map(ReqOutputMapModelProjection::keys)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .collect(Collectors.toSet());

    Set<ReqOutputKeyProjection> keys2 = mps2.stream()
        .map(ReqOutputMapModelProjection::keys)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .collect(Collectors.toSet());

    return keys1.equals(keys2) && super.mapModelEquals(mps1, mps2);

  }
}
