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

package ws.epigraph.projections.req.input;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.req.AbstractReqProjectionsPrettyPrinter;
import ws.epigraph.projections.req.ReqParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqInputProjectionsPrettyPrinter<E extends Exception>
    extends AbstractReqProjectionsPrettyPrinter<
    ReqInputVarProjection,
    ReqInputTagProjectionEntry,
    ReqInputModelProjection<?, ?, ?>,
    ReqInputRecordModelProjection,
    ReqInputFieldProjectionEntry,
    ReqInputFieldProjection,
    E> {

  public ReqInputProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<ReqInputVarProjection, ReqInputModelProjection<?, ?, ?>> context) {
    super(layouter, context);
  }

  public ReqInputProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(Qn.EMPTY));
  }

  @Override
  public void printTag(@Nullable String tagName, @NotNull ReqInputTagProjectionEntry tp, int pathSteps) throws E {
    ReqInputModelProjection<?, ?, ?> projection = tp.projection();

    ReqParams params = projection.params();
    Annotations annotations = projection.annotations();

    l.beginIInd(0);
    boolean needBrk = false;

    if (tagName != null) {
      l.print(tagName);
      needBrk = true;
    }

    if (!params.isEmpty()) {
      printParams(params);
      needBrk = true;
    }

    if (!annotations.isEmpty()) {
      printAnnotations(annotations);
      needBrk = true;
    }

    if (!isPrintoutEmpty(projection)) {
      if (needBrk) l.brk();
      printModel(projection, pathSteps);
    }

    l.end();
  }

  @Override
  public void printModelOnly(@NotNull ReqInputModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqInputRecordModelProjection)
      print((ReqInputRecordModelProjection) mp, 0);
    else if (mp instanceof ReqInputMapModelProjection)
      printModelOnly((ReqInputMapModelProjection) mp);
    else if (mp instanceof ReqInputListModelProjection)
      printModelOnly((ReqInputListModelProjection) mp, pathSteps);
  }

  private void printModelOnly(ReqInputMapModelProjection mp) throws E {
    printMapModelProjection(mp.keys(), mp.itemsProjection());
  }

  private void printModelOnly(ReqInputListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(").brk();
    printVar(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

}
