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
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.AbstractOpProjectionsPrettyPrinter;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpProjectionsPrettyPrinter<E extends Exception>
    extends AbstractOpProjectionsPrettyPrinter<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?, ?>,
    OpOutputRecordModelProjection,
    OpOutputFieldProjectionEntry,
    OpOutputFieldProjection,
    E> {

  // todo + on tags/fields/maps/lists
  // todo defaults

  public OpProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<OpOutputVarProjection, OpOutputModelProjection<?, ?, ?, ?>> context) {
    super(layouter, context);
  }

  public OpProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.EMPTY, null));
  }

  @Override
  protected boolean printModelParamsInBlock(final @NotNull OpOutputModelProjection<?, ?, ?, ?> projection) throws E {
    final OpParams params = projection.params();
    final Annotations annotations = projection.annotations();
    final GDatum defaultValue = projection.defaultValue();
    final OpOutputModelProjection<?, ?, ?, ?> metaProjection = projection.metaProjection();

    boolean first = true;
    if (!params.isEmpty())
      //noinspection ConstantConditions
      first = printOpParams(params, false, first);
    if (!annotations.isEmpty())
      first = printAnnotations(annotations, false, first);

    if (defaultValue != null) {
      if (first)
        first = false;
      else {
        l.print(",");
        brk();
      }
      l.beginIInd(0).print("default:");
      brk();
      gdataPrettyPrinter.print(defaultValue);
      l.end();
    }

    if (metaProjection != null) {
      if (first)
        first = false;
      else
        brk();

      l.beginIInd(0).print("meta:");
      brk();
      if (metaProjection.flagged()) l.print("+");
      printModel(metaProjection, 0);
      l.end();
    }

    return first;
  }

  @Override
  protected void printTagName(@NotNull String tagName, @NotNull OpOutputModelProjection<?, ?, ?, ?> mp) throws E {
    if (mp.flagged()) l.print("+");
    l.print(escape(tagName));
  }

  @Override
  protected String fieldNamePrefix(final @NotNull OpOutputFieldProjectionEntry fieldEntry) {
    return fieldEntry.fieldProjection().varProjection().flagged() ? "+" : "";
    // todo: don't print '+' for vars/models if printed for field
  }

  @Override
  public void printModelOnly(@NotNull OpOutputModelProjection<?, ?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpOutputRecordModelProjection)
      printRecordProjection((OpOutputRecordModelProjection) mp);
    else if (mp instanceof OpOutputMapModelProjection)
      printModelOnly((OpOutputMapModelProjection) mp);
    else if (mp instanceof OpOutputListModelProjection)
      printModelOnly((OpOutputListModelProjection) mp);
  }

  private void printModelOnly(OpOutputMapModelProjection mp) throws E {
    @NotNull OpOutputKeyProjection keyProjection = mp.keyProjection();
    OpOutputVarProjection itemsProjection = mp.itemsProjection();

    printMapModelProjection(
        keyProjection.presence().getPrettyPrinterString(),
        keyProjection,
        itemsProjection.flagged() ? "+" : "",
        // todo: don't print '+' for vars/models if printed for items
        itemsProjection
    );
  }

  private void printModelOnly(OpOutputListModelProjection mp) throws E {
    l.beginIInd();
    l.print("*");
    if (mp.itemsProjection().flagged()) l.print("+");
    // todo: don't print '+' for vars/models if printed for items
    l.print("(");
    brk();
    printVar(mp.itemsProjection(), 0);
    brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  @Override
  public boolean isPrintoutNoParamsEmpty(@NotNull OpOutputModelProjection<?, ?, ?, ?> mp) {
    if (mp instanceof OpOutputMapModelProjection) {
      OpOutputMapModelProjection mapModelProjection = (OpOutputMapModelProjection) mp;
      /*@NotNull*/
      OpOutputKeyProjection keyProjection = mapModelProjection.keyProjection();

      if (keyProjection.presence() != OpKeyPresence.OPTIONAL) return false;
      if (!keyProjection.params().isEmpty()) return false;
      if (!keyProjection.annotations().isEmpty()) return false;

      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    } else
      return super.isPrintoutNoParamsEmpty(mp);
  }

  @Override
  public boolean modelParamsEmpty(final @NotNull OpOutputModelProjection<?, ?, ?, ?> projection) {
    return projection.defaultValue() == null && super.modelParamsEmpty(projection);
  }
}
