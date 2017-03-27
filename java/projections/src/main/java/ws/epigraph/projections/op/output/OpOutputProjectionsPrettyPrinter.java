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

package ws.epigraph.projections.op.output;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.op.AbstractOpProjectionsPrettyPrinter;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParams;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsPrettyPrinter<E extends Exception>
    extends AbstractOpProjectionsPrettyPrinter<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?>,
    OpOutputRecordModelProjection,
    OpOutputFieldProjectionEntry,
    OpOutputFieldProjection,
    E> {

  public OpOutputProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?>> context) {
    super(layouter, context);
  }

  public OpOutputProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(Qn.EMPTY));
  }

  @Override
  public void printModel(@Nullable String tagName, @NotNull OpOutputTagProjectionEntry tp, int pathSteps) throws E {
    OpOutputModelProjection<?, ?, ?> projection = tp.projection();
    OpOutputModelProjection<?, ?, ?> metaProjection = projection.metaProjection();
    OpParams params = projection.params();
    Annotations annotations = projection.annotations();

    if (params.isEmpty() && annotations.isEmpty() && metaProjection == null) {
      l.beginIInd(0);

      if (!isPrintoutEmpty(projection)) {
        if (tagName != null) {
          l.print(escape(tagName));
          l.brk();
        }
        printModel(projection, 0);
      } else if (tagName != null) l.print(escape(tagName));

      l.end();
    } else {
      l.beginCInd();

      if (tagName == null) l.print("{");
      else {
        l.print(escape(tagName));
        l.print(" {");
      }

      if (!params.isEmpty()) print(params);
      if (!annotations.isEmpty()) printModel(annotations);

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
  public void printModelOnly(@NotNull OpOutputModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpOutputRecordModelProjection)
      print((OpOutputRecordModelProjection) mp);
    else if (mp instanceof OpOutputMapModelProjection)
      printModelOnly((OpOutputMapModelProjection) mp);
    else if (mp instanceof OpOutputListModelProjection)
      printModelOnly((OpOutputListModelProjection) mp);
  }

  private void printModelOnly(OpOutputMapModelProjection mp) throws E {
    @NotNull OpOutputKeyProjection keyProjection = mp.keyProjection();
    printMapModelProjection(keyProjection.presence().getPrettyPrinterString(), keyProjection, mp.itemsProjection());
  }

  private void printModelOnly(OpOutputListModelProjection mp) throws E {
    l.beginIInd();
    l.print("*(").brk();
    printModel(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  @Override
  public boolean isPrintoutEmpty(@NotNull OpOutputModelProjection<?, ?, ?> mp) {
    if (mp instanceof OpOutputRecordModelProjection) {
      OpOutputRecordModelProjection recordModelProjection = (OpOutputRecordModelProjection) mp;
      Map<String, OpOutputFieldProjectionEntry> fieldProjections = recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    if (mp instanceof OpOutputMapModelProjection) {
      OpOutputMapModelProjection mapModelProjection = (OpOutputMapModelProjection) mp;
      @NotNull OpOutputKeyProjection keyProjection = mapModelProjection.keyProjection();

      if (keyProjection.presence() != OpKeyPresence.OPTIONAL) return false;
      if (!keyProjection.params().isEmpty()) return false;
      if (!keyProjection.annotations().isEmpty()) return false;

      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    if (mp instanceof OpOutputListModelProjection) {
      OpOutputListModelProjection outputListModelProjection = (OpOutputListModelProjection) mp;
      return isPrintoutEmpty(outputListModelProjection.itemsProjection());
    }

    return true;
  }
}
