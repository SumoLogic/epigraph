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
import ws.epigraph.projections.gen.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AbstractReqProjectionsComparator<
    VP extends GenEntityProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends AbstractReqModelProjection</*MP*/?, /*SMP*/?, /*M*/?>,
    RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, ?>,
    MMP extends GenMapModelProjection<VP, TP, MP, MMP, ?>,
    LMP extends GenListModelProjection<VP, TP, MP, LMP, ?>,
    PMP extends GenPrimitiveModelProjection<MP, PMP, ?>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends AbstractReqFieldProjection<VP, TP, MP, FP>
    > extends GenProjectionsComparator<VP, TP, MP, RMP, MMP, LMP, PMP, FPE, FP> {

  private final boolean compareAnnotations;
  private final boolean compareParams;

  public AbstractReqProjectionsComparator(final boolean compareAnnotations, final boolean compareParams) {
    this.compareAnnotations = compareAnnotations;
    this.compareParams = compareParams;
  }

  protected boolean compareAnnotations() { return compareAnnotations; }

  protected boolean compareParams() { return compareParams; }

  @Override
  protected boolean modelEquals(
      final @NotNull Collection<@NotNull MP> mps1,
      final @NotNull Collection<@NotNull MP> mps2) {

    if (compareAnnotations()) {
      final List<Directives> as1 = mps1.stream().map(MP::directives).collect(Collectors.toList());
      final List<Directives> as2 = mps2.stream().map(MP::directives).collect(Collectors.toList());
      if (!annotationsEqual(as1, as2))
        return false;
    }

    if (compareParams()) {
      final List<ReqParams> ps1 = mps1.stream().map(MP::params).collect(Collectors.toList());
      final List<ReqParams> ps2 = mps2.stream().map(MP::params).collect(Collectors.toList());
      if (!paramsEqual(ps1, ps2))
        return false;
    }

    return super.modelEquals(mps1, mps2);
  }

  protected boolean annotationsEqual(
      @NotNull Collection<@NotNull Directives> as1,
      @NotNull Collection<@NotNull Directives> as2) {

    if (as1.isEmpty())
      return as2.isEmpty();

    return Directives.merge(as1).equals(Directives.merge(as2));
  }

  protected boolean paramsEqual(
      @NotNull Collection<@NotNull ReqParams> ps1,
      @NotNull Collection<@NotNull ReqParams> ps2) {

    if (ps1.isEmpty())
      return ps2.isEmpty();

    return ReqParams.merge(ps1).equals(ReqParams.merge(ps2));
  }
}
