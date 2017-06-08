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

package ws.epigraph.projections.op.delete;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.AbstractOpProjectionsPrettyPrinter;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteProjectionsPrettyPrinter<E extends Exception>
    extends AbstractOpProjectionsPrettyPrinter<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?, ?>,
    OpDeleteRecordModelProjection,
    OpDeleteFieldProjectionEntry,
    OpDeleteFieldProjection,
    E> {

  public OpDeleteProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<OpDeleteVarProjection, OpDeleteModelProjection<?, ?, ?>> context) {
    super(layouter, context);
  }

  public OpDeleteProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.EMPTY, null));
  }

  @Override
  protected String fieldNamePrefix(final @NotNull OpDeleteFieldProjectionEntry fieldEntry) {
    return fieldEntry.fieldProjection().varProjection().canDelete() ? "+" : "";
  }

  @Override
  public void printModelOnly(@NotNull OpDeleteModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpDeleteRecordModelProjection)
      printRecordProjection((OpDeleteRecordModelProjection) mp);
    else if (mp instanceof OpDeleteMapModelProjection)
      printModelOnly((OpDeleteMapModelProjection) mp);
    else if (mp instanceof OpDeleteListModelProjection)
      printModelOnly((OpDeleteListModelProjection) mp);
  }

  private void printModelOnly(OpDeleteMapModelProjection mp) throws E {
    OpDeleteKeyProjection keyProjection = mp.keyProjection();
    OpDeleteVarProjection itemsProjection = mp.itemsProjection();

    printMapModelProjection(
        keyProjection.presence().getPrettyPrinterString(),
        keyProjection,
        itemsProjection.canDelete() ? "+" : "",
        itemsProjection
    );
  }

  private void printModelOnly(OpDeleteListModelProjection mp) throws E {
    l.beginIInd();
    l.print("*");
    if (mp.itemsProjection().canDelete()) l.print("+");
    l.print("(");
    brk();
    printVar(mp.itemsProjection(), 0);
    brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  @Override
  public void printOpParams(@NotNull OpParams p) throws E {
    printOpParams(p, false, true);
  }

  @Override
  public boolean printOpParams(@NotNull OpParams p, boolean needCommas, boolean first) throws E {
    l.beginCInd(0);
    for (OpParam param : p.asMap().values()) {
      if (first) {
        first = false;
      } else {
        if (needCommas) l.print(",");
        brk();
      }
      printOpParam(param);
    }
    l.end();

    return first;
  }


//  @Override
//  protected boolean isPrintoutEmpty(@NotNull OpDeleteVarProjection opDeleteVarProjection) {
//    return !opDeleteVarProjection.canDelete() && super.isPrintoutEmpty(opDeleteVarProjection);
//  }

  @Override
  public boolean isPrintoutNoParamsEmpty(@NotNull OpDeleteModelProjection<?, ?, ?> mp) {
    if (mp instanceof OpDeleteMapModelProjection) {
      OpDeleteMapModelProjection mapModelProjection = (OpDeleteMapModelProjection) mp;
      @NotNull OpDeleteKeyProjection keyProjection = mapModelProjection.keyProjection();

      if (keyProjection.presence() != OpKeyPresence.OPTIONAL) return false;
      if (!keyProjection.params().isEmpty()) return false;
      if (!keyProjection.annotations().isEmpty()) return false;

      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    } else
      return super.isPrintoutNoParamsEmpty(mp);
  }
}
