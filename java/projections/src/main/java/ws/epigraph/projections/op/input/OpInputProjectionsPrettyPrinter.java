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

package ws.epigraph.projections.op.input;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.AbstractOpProjectionsPrettyPrinter;
import ws.epigraph.projections.op.OpParams;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsPrettyPrinter<E extends Exception> extends AbstractOpProjectionsPrettyPrinter<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?>,
    OpInputRecordModelProjection,
    OpInputFieldProjectionEntry,
    OpInputFieldProjection,
    E> {

  public OpInputProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull String tagName, @NotNull OpInputTagProjectionEntry tp, int pathSteps) throws E {
    OpInputModelProjection<?, ?, ?> projection = tp.projection();
    OpInputModelProjection<?, ?, ?> metaProjection = projection.metaProjection();
    final OpParams params = projection.params();
    final Annotations annotations = projection.annotations();

    if (projection.defaultValue() == null && annotations.isEmpty() && params.isEmpty() &&
        metaProjection == null) {

      l.beginCInd();
      if (projection.required()) l.print("+");
      l.print(tagName);

      if (!isPrintoutEmpty(projection)) {
        l.brk();
        print(projection, pathSteps);
      }
      l.end();
    } else {
      l.beginCInd();
      if (projection.required()) l.print("+");
      l.print(tagName);
      l.print(" {");

      if (projection.defaultValue() != null) {
        l.brk().beginIInd(0).print("default:").brk();
        dataPrinter.print(projection.defaultValue());
        l.end();
      }

      if (metaProjection != null) {
        l.brk().beginIInd(0).print("meta:").brk();
        print(metaProjection, 0);
        l.end();
      }

      if (!params.isEmpty()) print(params);
      if (!annotations.isEmpty()) print(annotations);

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
  public void print(@NotNull OpInputModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpInputRecordModelProjection)
      print((OpInputRecordModelProjection) mp);
    else if (mp instanceof OpInputMapModelProjection)
      print((OpInputMapModelProjection) mp);
    else if (mp instanceof OpInputListModelProjection)
      print((OpInputListModelProjection) mp);
  }
  @Override
  protected String fieldNamePrefix(@NotNull final OpInputFieldProjectionEntry fieldEntry) {
    return fieldEntry.fieldProjection().required() ? "+" : "";
  }

  private void print(OpInputMapModelProjection mp) throws E {
    printMapModelProjection(null, mp.keyProjection(), mp.itemsProjection());
  }

  private void print(OpInputListModelProjection mp) throws E {
    l.beginIInd();
    l.print("*(").brk();
    print(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }


  @Override
  public boolean isPrintoutEmpty(@NotNull OpInputModelProjection<?, ?, ?> mp) {
    if (mp instanceof OpInputRecordModelProjection) {
      OpInputRecordModelProjection recordModelProjection = (OpInputRecordModelProjection) mp;
      Map<String, OpInputFieldProjectionEntry> fieldProjections = recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    if (mp instanceof OpInputMapModelProjection) {
      OpInputMapModelProjection mapModelProjection = (OpInputMapModelProjection) mp;
      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    if (mp instanceof OpInputListModelProjection) {
      OpInputListModelProjection inputListModelProjection = (OpInputListModelProjection) mp;
      return isPrintoutEmpty(inputListModelProjection.itemsProjection());
    }

    return true;
  }

}
