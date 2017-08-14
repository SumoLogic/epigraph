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

package ws.epigraph.projections.req.delete;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.AbstractReqProjectionsPrettyPrinter;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteProjectionsPrettyPrinter<E extends Exception>
    extends AbstractReqProjectionsPrettyPrinter<
    ReqDeleteVarProjection,
    ReqDeleteTagProjectionEntry,
    ReqDeleteModelProjection<?, ?, ?>,
    ReqDeleteRecordModelProjection,
    ReqDeleteFieldProjectionEntry,
    ReqDeleteFieldProjection,
    E> {

  public ReqDeleteProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<ReqDeleteVarProjection, ReqDeleteModelProjection<?, ?, ?>> context) {
    super(layouter, context);
  }

  public ReqDeleteProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.EMPTY, null));
  }

  @Override
  public void printModelOnly(@NotNull ReqDeleteModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqDeleteRecordModelProjection)
      print((ReqDeleteRecordModelProjection) mp, 0);
    else if (mp instanceof ReqDeleteMapModelProjection)
      printModelOnly((ReqDeleteMapModelProjection) mp);
    else if (mp instanceof ReqDeleteListModelProjection)
      printModelOnly((ReqDeleteListModelProjection) mp, pathSteps);
  }

  private void printModelOnly(ReqDeleteMapModelProjection mp) throws E {
    printMapModelProjection(mp.keys(), mp.itemsProjection());
  }

  private void printModelOnly(ReqDeleteListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(");
    brk();
    printVar(mp.itemsProjection(), 0);
    brk(1, -l.getDefaultIndentation()).end().print(")");
  }


  @Override
  public boolean isPrintoutNoParamsEmpty(@NotNull ReqDeleteModelProjection<?, ?, ?> mp) {
    if (mp instanceof ReqDeleteMapModelProjection) {
      ReqDeleteMapModelProjection mapModelProjection = (ReqDeleteMapModelProjection) mp;
      /*@Nullable*/ List<ReqDeleteKeyProjection> keys = mapModelProjection.keys();
      return keys == null && isPrintoutEmpty(mapModelProjection.itemsProjection());
    } else
      return super.isPrintoutNoParamsEmpty(mp);
  }

}
