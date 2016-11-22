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

package ws.epigraph.projections.op.delete;

import de.uka.ilkd.pp.Layouter;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.AbstractOpProjectionsPrettyPrinter;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteProjectionsPrettyPrinter<E extends Exception>
    extends AbstractOpProjectionsPrettyPrinter<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>,
    OpDeleteRecordModelProjection,
    OpDeleteFieldProjectionEntry,
    OpDeleteFieldProjection,
    E> {

  public OpDeleteProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  protected void printVarDecoration(@NotNull OpDeleteVarProjection p) throws E {
    if (p.canDelete()) l.print("+");
  }

  @Override
  public void print(@NotNull String tagName, @NotNull OpDeleteTagProjectionEntry tp, int pathSteps) throws E {
    OpDeleteModelProjection<?, ?> projection = tp.projection();
    OpDeleteModelProjection<?, ?> metaProjection = projection.metaProjection();
    OpParams params = projection.params();
    Annotations annotations = projection.annotations();

    if (params.isEmpty() && annotations.isEmpty()) {
      l.beginCInd();
      l.print(tagName);

      if (!isPrintoutEmpty(projection)) {
        l.brk();
        print(projection, 0);
      }

      l.end();
    } else {
      l.beginCInd();
      l.print(tagName);
      l.print(" {");

      if (!params.isEmpty()) print(params);
      if (!annotations.isEmpty()) print(annotations);

      if (metaProjection != null) {
        l.brk().beginIInd(0).print("meta:").brk();
        print(metaProjection, 0);
        l.end();
      }

      l.brk(1, -l.getDefaultIndentation()).end().print("}");

      if (!isPrintoutEmpty(projection)) {
        l.beginIInd();
        l.brk();
        print(projection, 0);
        l.end();
      }
    }
  }

  @Override
  public void print(@NotNull OpDeleteModelProjection<?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpDeleteRecordModelProjection)
      print((OpDeleteRecordModelProjection) mp);
    else if (mp instanceof OpDeleteMapModelProjection)
      print((OpDeleteMapModelProjection) mp);
    else if (mp instanceof OpDeleteListModelProjection)
      print((OpDeleteListModelProjection) mp);
  }

  private void print(OpDeleteMapModelProjection mp) throws E {
    @NotNull OpDeleteKeyProjection keyProjection = mp.keyProjection();
    printMapModelProjection(keyProjection.presence().getPrettyPrinterString(), keyProjection, mp.itemsProjection());
  }

  private void print(OpDeleteListModelProjection mp) throws E {
    l.beginIInd();
    l.print("*(").brk();
    print(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  public void print(@NotNull OpParams p) throws E {
    print(p, false, true);
  }

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
  public boolean isPrintoutEmpty(@NotNull OpDeleteModelProjection<?, ?> mp) {
    if (mp instanceof OpDeleteRecordModelProjection) {
      OpDeleteRecordModelProjection recordModelProjection = (OpDeleteRecordModelProjection) mp;
      Map<String, OpDeleteFieldProjectionEntry> fieldProjections = recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    if (mp instanceof OpDeleteMapModelProjection) {
      OpDeleteMapModelProjection mapModelProjection = (OpDeleteMapModelProjection) mp;
      @NotNull OpDeleteKeyProjection keyProjection = mapModelProjection.keyProjection();

      if (keyProjection.presence() != OpKeyPresence.OPTIONAL) return false;
      if (!keyProjection.params().isEmpty()) return false;
      if (!keyProjection.annotations().isEmpty()) return false;

      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    if (mp instanceof OpDeleteListModelProjection) {
      OpDeleteListModelProjection deleteListModelProjection = (OpDeleteListModelProjection) mp;
      return isPrintoutEmpty(deleteListModelProjection.itemsProjection());
    }

    return true;
  }
}
