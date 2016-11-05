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

package ws.epigraph.projections.op;

import de.uka.ilkd.pp.Layouter;
import ws.epigraph.data.Datum;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPrettyPrinter;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractOpProjectionsPrettyPrinter<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>,
    FP extends AbstractOpFieldProjection<VP, TP, MP>,
    E extends Exception> extends AbstractProjectionsPrettyPrinter<VP, TP, MP, E> {

  protected AbstractOpProjectionsPrettyPrinter(final Layouter<E> layouter) {
    super(layouter);
  }

  public void print(@NotNull OpParam p) throws E {
    OpInputModelProjection<?, ?, ?> projection = p.projection();

    l.beginIInd();
    l.print(";");
    if (projection.required()) l.print("+");
    l.print(p.name()).print(":").brk();
    l.print(projection.model().name().toString());

    OpInputProjectionsPrettyPrinter<E> ipp = new OpInputProjectionsPrettyPrinter<>(l);
    if (!ipp.isPrintoutEmpty(projection)) {
      l.brk();
      ipp.print(projection, 0);
    }

    Annotations annotations = projection.annotations();

    Datum defaultValue = projection.defaultValue();
    if (defaultValue != null) {
      l.brk().print("=").brk();
      dataPrinter.print(defaultValue);
    }

    if (!annotations.isEmpty()) {
      l.beginCInd();
      l.print(" {");
      print(annotations);
      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    }

    l.end();
  }

  public abstract void print(@NotNull FP fieldProjection) throws E;

  public void print(@NotNull String prefix, @NotNull FP fieldProjection) throws E {
    if (isPrintoutEmpty(fieldProjection)) {
      l.print(prefix);
    } else {
      boolean isBlock = isBlockProjection(fieldProjection);

      if (!isBlock) l.beginIInd();
      l.print(prefix);

      if (isBlock) l.print(" ");
      else l.brk();

      print(fieldProjection);
      if (!isBlock) l.end();
    }
  }

  public boolean isPrintoutEmpty(@NotNull FP fieldProjection) {
    @NotNull VP fieldVarProjection = fieldProjection.projection();
    @NotNull OpParams fieldParams = fieldProjection.params();
    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    return fieldParams.isEmpty() && fieldAnnotations.isEmpty() && isPrintoutEmpty(fieldVarProjection);
  }

  public boolean isBlockProjection(@NotNull FP fieldProjection) {
    return !fieldProjection.params().isEmpty() || !fieldProjection.annotations().isEmpty();
  }

}
