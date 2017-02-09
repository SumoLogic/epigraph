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

package ws.epigraph.projections.op.path;

import de.uka.ilkd.pp.Layouter;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.AbstractOpProjectionsPrettyPrinter;
import ws.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPathPrettyPrinter<E extends Exception>
    extends AbstractOpProjectionsPrettyPrinter<
    OpVarPath,
    OpTagPath,
    OpModelPath<?, ?, ?>,
    OpRecordModelPath,
    OpFieldPathEntry,
    OpFieldPath,
    E> {

  public OpPathPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  protected void printVarOnly(@NotNull OpVarPath p, int pathSteps) throws E {
    // no tags = end of path
    if (!p.tagProjections().isEmpty()) {
      super.printVarOnly(p, pathSteps);
    }
  }

  @Override
  public void print(@Nullable String tagName, @NotNull OpTagPath tp, int pathSteps) throws E {
    OpModelPath<?, ?, ?> projection = tp.projection();
    OpParams params = projection.params();
    Annotations annotations = projection.annotations();

    boolean brkNeeded = false;
    if (!params.isEmpty() || !annotations.isEmpty()) {

      l.beginCInd();

      if (tagName == null) l.print("{");
      else {
        l.print(escape(tagName));
        l.print(" {");
      }

      if (!params.isEmpty()) print(params);
      if (!annotations.isEmpty()) print(annotations);

      l.brk(1, -l.getDefaultIndentation()).end().print("}");
      brkNeeded = true;
    } else if (tagName != null) {
      l.print(escape(tagName));
      brkNeeded = true;
    }

    if (!isPrintoutEmpty(projection)) {
      l.beginCInd();
      if (brkNeeded) l.brk();
      print(projection, 0);
      l.end();
    }

  }

  @Override
  public void print(@NotNull OpModelPath<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpRecordModelPath)
      print((OpRecordModelPath) mp);
    else if (mp instanceof OpMapModelPath)
      print((OpMapModelPath) mp);
  }

  @Override
  public void print(@NotNull OpRecordModelPath mp) throws E {
    final @Nullable OpFieldPathEntry entry = mp.pathFieldProjection();

    if (entry != null) {
      l.beginIInd();
      l.print("/").brk();

      print(entry.field().name(), entry.fieldProjection());
      l.end();
    }
  }

  private void print(OpMapModelPath mp) throws E {
    l.beginIInd(0);

    @NotNull OpPathKeyProjection keyProjection = mp.keyProjection();
    @NotNull OpParams keyParams = keyProjection.params();
    @NotNull Annotations keyAnnotations = keyProjection.annotations();

    l.print("/").brk().print(".");

    if (!keyParams.isEmpty() || !keyAnnotations.isEmpty()) {
      l.beginCInd();
      l.brk().print("{");

      boolean commaNeeded = false;

      if (!keyParams.isEmpty()) {
        print(keyParams, true, true);
        commaNeeded = !keyParams.isEmpty();
      }

      if (!keyAnnotations.isEmpty()) print(keyAnnotations, true, !commaNeeded);

      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    } else {
      if (!isPrintoutEmpty(mp.itemsProjection())) l.brk();
    }

    print(mp.itemsProjection(), 0);

    l.end();
  }

  @Override
  protected boolean isPrintoutEmpty(@NotNull OpVarPath opVarPath) {
    return OpVarPath.isEnd(opVarPath) /*|| super.isPrintoutEmpty(opVarPath)*/;
  }

  @Override
  public boolean isPrintoutEmpty(@NotNull OpModelPath<?, ?, ?> mp) {
    if (mp instanceof OpRecordModelPath) {
      OpRecordModelPath recordModelProjection = (OpRecordModelPath) mp;
      return recordModelProjection.pathFieldProjection() == null;
    }

    if (mp instanceof OpMapModelPath) {
//      OpMapModelPath mapModelProjection = (OpMapModelPath) mp;
//      @NotNull OpPathKeyProjection keyProjection = mapModelProjection.keyProjection();
//
//      if (!keyProjection.params().isEmpty()) return false;
//      if (!keyProjection.annotations().isEmpty()) return false;
//
//      return isPrintoutEmpty(mapModelProjection.itemsProjection());
      return false;
    }

    return true;
  }
}
