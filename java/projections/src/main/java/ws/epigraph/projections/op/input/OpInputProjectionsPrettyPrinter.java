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

package ws.epigraph.projections.op.input;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.op.AbstractOpProjectionsPrettyPrinter;
import ws.epigraph.projections.op.OpParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsPrettyPrinter<E extends Exception> extends AbstractOpProjectionsPrettyPrinter<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?, ?>,
    OpInputRecordModelProjection,
    OpInputFieldProjectionEntry,
    OpInputFieldProjection,
    E> {

  public OpInputProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<OpInputVarProjection, OpInputModelProjection<?, ?, ?, ?>> context) {
    super(layouter, context);
  }

  public OpInputProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(Qn.EMPTY));
  }

  @Override
  protected boolean printModelParamsInBlock(final @NotNull OpInputModelProjection<?, ?, ?, ?> projection) throws E {
    final OpParams params = projection.params();
    final Annotations annotations = projection.annotations();
    final GDatum defaultValue = projection.defaultValue();
    final OpInputModelProjection<?, ?, ?, ?> metaProjection = projection.metaProjection();

    boolean first = true;
    if (!params.isEmpty())
      //noinspection ConstantConditions
      first = printOpParams(params, false, first);
    if (!annotations.isEmpty())
      first = printAnnotations(annotations, false, first);

    if (defaultValue != null) {
      if (first)
        first = false;
      else
        l.brk();
      l.beginIInd(0).print("default:").brk();
      gdataPrettyPrinter.print(defaultValue);
      l.end();
    }

    if (metaProjection != null) {
      if (first)
        first = false;
      else
        l.brk();

      l.beginIInd(0).print("meta:").brk();
      if (metaProjection.required()) l.print("+");
      printModel(metaProjection, 0);
      l.end();
    }

    return first;
  }

  @Override
  protected void printTagName(@NotNull String tagName, @NotNull OpInputModelProjection<?, ?, ?, ?> mp) throws E {
    if (mp.required) l.print("+");
    l.print(escape(tagName));
  }

  @Override
  public void printModelOnly(@NotNull OpInputModelProjection<?, ?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpInputRecordModelProjection)
      printRecordProjection((OpInputRecordModelProjection) mp);
    else if (mp instanceof OpInputMapModelProjection)
      printModelOnly((OpInputMapModelProjection) mp);
    else if (mp instanceof OpInputListModelProjection)
      printModelOnly((OpInputListModelProjection) mp);
  }

  @Override
  protected String fieldNamePrefix(final @NotNull OpInputFieldProjectionEntry fieldEntry) {
    return fieldEntry.fieldProjection().required() ? "+" : "";
  }

  private void printModelOnly(OpInputMapModelProjection mp) throws E {
    @NotNull OpInputKeyProjection keyProjection = mp.keyProjection();
    printMapModelProjection(
        keyProjection.presence().getPrettyPrinterString(),
        mp.keyProjection(),
        mp.itemsProjection()
    );
  }

  private void printModelOnly(OpInputListModelProjection mp) throws E {
    l.beginIInd();
    l.print("*(").brk();
    printVar(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  @Override
  public boolean modelParamsEmpty(final @NotNull OpInputModelProjection<?, ?, ?, ?> projection) {
    return projection.defaultValue() == null && super.modelParamsEmpty(projection);
  }
}
