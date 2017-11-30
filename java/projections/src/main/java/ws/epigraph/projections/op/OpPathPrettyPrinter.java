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

package ws.epigraph.projections.op;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPathPrettyPrinter<E extends Exception>
    extends AbstractOpProjectionsPrettyPrinter<E> {

  public OpPathPrettyPrinter(Layouter<E> layouter) {
    super(layouter, new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.EMPTY, null));
  }

  @Override
  protected void printProjectionWithoutTails(@NotNull OpProjection<?, ?> p, int pathSteps) throws E {
    // no tags = end of path
    if (!p.tagProjections().isEmpty()) {
      super.printProjectionWithoutTails(p, pathSteps);
    }
  }

  @Override
  public void printModelOnly(@NotNull OpModelProjection<?, ?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpRecordModelProjection)
      printRecordProjection((OpRecordModelProjection) mp);
    else if (mp instanceof OpMapModelProjection)
      printModelOnly((OpMapModelProjection) mp);
  }

  @Override
  public void printRecordProjection(@NotNull OpRecordModelProjection mp) throws E {
    final /*@Nullable*/ OpFieldProjectionEntry entry = mp.pathFieldProjection();

    if (entry != null) {
      l.beginIInd();
      l.print("/");
      brk();

      printFieldProjection(entry.field().name(), entry.fieldProjection());
      l.end();
    }
  }

  private void printModelOnly(OpMapModelProjection mp) throws E {
    l.beginIInd(0);

    @NotNull OpKeyProjection keyProjection = mp.keyProjection();
    @NotNull OpParams keyParams = keyProjection.params();
    @NotNull Annotations keyAnnotations = keyProjection.annotations();

    l.print("/");
    brk().print(".");

    if (!keyParams.isEmpty() || !keyAnnotations.isEmpty()) {
      l.beginCInd();
      brk().print("[");

      boolean commaNeeded = false;

      if (!keyParams.isEmpty()) {
        printOpParams(keyParams, true, true);
        commaNeeded = !keyParams.isEmpty();
      }

      if (!keyAnnotations.isEmpty()) printAnnotations(keyAnnotations, true, !commaNeeded);

      brk(1, -l.getDefaultIndentation()).end().print("]");
    } else {
      if (!isPrintoutEmpty(mp.itemsProjection())) brk();
    }

    printProjection(mp.itemsProjection(), 0);

    l.end();
  }

  @Override
  protected boolean isPrintoutEmpty(@NotNull OpEntityProjection opVarPath) {
    return opVarPath.isPathEnd() /*|| super.isPrintoutEmpty(opVarPath)*/;
  }

  @Override
  public boolean isPrintoutNoParamsEmpty(@NotNull OpModelProjection<?, ?, ?, ?> mp) {
    if (mp instanceof OpMapModelProjection) {
//      OpMapModelProjection mapModelProjection = (OpMapModelProjection) mp;
//      @NotNull OpPathKeyProjection keyProjection = mapModelProjection.keyProjection();
//
//      if (!keyProjection.params().isEmpty()) return false;
//      if (!keyProjection.annotations().isEmpty()) return false;
//
//      return isPrintoutEmpty(mapModelProjection.itemsProjection());
      return false;
    }

    return super.isPrintoutNoParamsEmpty(mp);
  }
}
