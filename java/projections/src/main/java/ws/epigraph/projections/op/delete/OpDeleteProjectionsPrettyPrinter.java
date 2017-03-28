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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
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
      final @NotNull ProjectionsPrettyPrinterContext<OpDeleteVarProjection, OpDeleteModelProjection<?,?,?>> context) {
    super(layouter, context);
  }
  public OpDeleteProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(Qn.EMPTY));
  }

  @Override
  protected void printVarDecoration(@NotNull OpDeleteVarProjection p) throws E {
    if (p.canDelete()) l.print("+");
  }

  @Override
  public void printTag(@Nullable String tagName, @NotNull OpDeleteTagProjectionEntry tp, int pathSteps) throws E {
    OpDeleteModelProjection<?, ?, ?> projection = tp.projection();
    OpDeleteModelProjection<?, ?, ?> metaProjection = projection.metaProjection();
    OpParams params = projection.params();
    Annotations annotations = projection.annotations();

    if (params.isEmpty() && annotations.isEmpty()) {
      l.beginIInd(0);

      if (!isPrintoutEmpty(projection)) {
        if (tagName != null) {
          l.print(escape(tagName));
          l.brk();
        }
        printModel(projection, 0);
      } else if (tagName!=null) l.print(escape(tagName));

      l.end();
    } else {
      l.beginCInd();

      if (tagName == null) l.print("{");
      else {
        l.print(escape(tagName));
        l.print(" {");
      }

      if (!params.isEmpty()) print(params);
      if (!annotations.isEmpty()) printAnnotations(annotations);

      if (metaProjection != null) {
        l.brk().beginIInd(0).print("meta:").brk();
        printModel(metaProjection, 0);
        l.end();
      }

      l.brk(1, -l.getDefaultIndentation()).end().print("}");

      if (!isPrintoutEmpty(projection)) {
        l.beginIInd();
        l.brk();
        printModel(projection, 0);
        l.end();
      }
    }
  }

  @Override
  public void printModelOnly(@NotNull OpDeleteModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpDeleteRecordModelProjection)
      print((OpDeleteRecordModelProjection) mp);
    else if (mp instanceof OpDeleteMapModelProjection)
      printModelOnly((OpDeleteMapModelProjection) mp);
    else if (mp instanceof OpDeleteListModelProjection)
      printModelOnly((OpDeleteListModelProjection) mp);
  }

  private void printModelOnly(OpDeleteMapModelProjection mp) throws E {
    @NotNull OpDeleteKeyProjection keyProjection = mp.keyProjection();
    printMapModelProjection(keyProjection.presence().getPrettyPrinterString(), keyProjection, mp.itemsProjection());
  }

  private void printModelOnly(OpDeleteListModelProjection mp) throws E {
    l.beginIInd();
    l.print("*(").brk();
    printVar(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  @Override
  public void print(@NotNull OpParams p) throws E {
    print(p, false, true);
  }

  @Override
  public boolean print(@NotNull OpParams p, boolean needCommas, boolean first) throws E {
    l.beginCInd(0);
    for (OpParam param : p.asMap().values()) {
      if (needCommas) {
        if (first) first = false;
        else l.print(",");
      }
      l.brk();
      print(param);
    }
    l.end();

    return first;
  }


  @Override
  protected boolean isPrintoutEmpty(@NotNull OpDeleteVarProjection opDeleteVarProjection) {
    return !opDeleteVarProjection.canDelete() && super.isPrintoutEmpty(opDeleteVarProjection);
  }

  @Override
  public boolean isPrintoutEmpty(@NotNull OpDeleteModelProjection<?, ?, ?> mp) {
    if (mp instanceof OpDeleteMapModelProjection) {
      OpDeleteMapModelProjection mapModelProjection = (OpDeleteMapModelProjection) mp;
      @NotNull OpDeleteKeyProjection keyProjection = mapModelProjection.keyProjection();

      if (keyProjection.presence() != OpKeyPresence.OPTIONAL) return false;
      if (!keyProjection.params().isEmpty()) return false;
      if (!keyProjection.annotations().isEmpty()) return false;

      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    } else
      return super.isPrintoutEmpty(mp);
  }
}
