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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.projections.gen.GenFieldProjectionEntry;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPrettyPrinter;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractOpProjectionsPrettyPrinter<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends AbstractOpModelProjection</*MP*/?, ?, ?>,
    RP extends GenRecordModelProjection<VP, TP, MP, RP, FPE, FP, ?>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends AbstractOpFieldProjection<VP, TP, MP, FP>,
    E extends Exception> extends AbstractProjectionsPrettyPrinter<VP, TP, MP, E> {

  protected AbstractOpProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<VP> context) {
    super(layouter, context);
  }

  public void print(@NotNull OpParams p) throws E {
    print(p, false, true);
  }

  public boolean print(@NotNull OpParams p, boolean needCommas, boolean first) throws E {
    for (OpParam param : p.asMap().values()) {
      if (needCommas) {
        if (first) first = false;
        else l.print(",");
      }
      l.brk();
      l.beginCInd(0);
      print(param);
      l.end();
    }

    return first;
  }

  public void print(@NotNull OpParam p) throws E {
    OpInputModelProjection<?, ?, ?, ?> projection = p.projection();

    l.beginIInd();
    l.print(";");
    if (projection.required()) l.print("+");
    l.print(escape(p.name())).print(":").brk();
    l.print(projection.type().name().toString());

    OpInputProjectionsPrettyPrinter<E> ipp = new OpInputProjectionsPrettyPrinter<>(l);
    if (!ipp.isPrintoutEmpty(projection)) {
      l.brk();
      ipp.print(projection, 0);
    }

    Annotations annotations = projection.annotations();

    GDatum defaultValue = projection.defaultValue();
    if (defaultValue != null) {
      l.brk().print("=").brk();
      gdataPrettyPrinter.print(defaultValue);
    }

    if (!annotations.isEmpty()) {
      l.beginCInd();
      l.print(" {");
      print(annotations);
      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    }

    l.end();
  }

  public void print(@NotNull RP recordProjection) throws E {
    @SuppressWarnings("unchecked")
    Map<String, FPE> fieldProjections = recordProjection.fieldProjections();

    l.print("(").beginCInd();
    boolean first = true;
    for (Map.Entry<String, FPE> entry : fieldProjections.entrySet()) {
      if (first) first = false;
      else l.print(",");
      l.brk();

      @NotNull String prefix = fieldNamePrefix(entry.getValue());
      @NotNull FP fieldProjection = entry.getValue().fieldProjection();

      print(prefix + escape(entry.getKey()), fieldProjection);
    }
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  protected String fieldNamePrefix(@NotNull FPE fieldEntry) { return ""; }

  public void print(@NotNull FP fieldProjection) throws E {
    @NotNull VP fieldVarProjection = fieldProjection.varProjection();
//    @NotNull OpParams fieldParams = fieldProjection.params();
//    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

//    if (fieldParams.isEmpty() && fieldAnnotations.isEmpty()) {
    if (!isPrintoutEmpty(fieldVarProjection)) {
      print(fieldVarProjection, 0);
    }
//    } else {
//      l.beginCInd();
//      l.print("{");
//      if (!fieldParams.isEmpty()) print(fieldParams);
//      if (!fieldAnnotations.isEmpty()) print(fieldAnnotations);
//      l.brk(1, -l.getDefaultIndentation()).end().print("}");
//      if (!isPrintoutEmpty(fieldVarProjection)) {
//        l.beginIInd();
//        l.brk();
//        print(fieldVarProjection, 0);
//        l.end();
//      }
//    }
  }

  protected void printMapModelProjection(
      @Nullable String keysProjectionPrefix,
      @NotNull OpKeyProjection keyProjection,
      VP itemsProjection) throws E {

    l.beginIInd();
    { // keys
      l.beginCInd();
      l.print("[");
      boolean commaNeeded = false;

      if (keysProjectionPrefix != null) {
        l.brk().print(keysProjectionPrefix);
        commaNeeded = true;
      }

      @NotNull OpParams keyParams = keyProjection.params();
      if (!keyParams.isEmpty()) {
        print(keyParams, true, !commaNeeded);
        commaNeeded = !keyParams.isEmpty();
      }

      @NotNull Annotations keyAnnotations = keyProjection.annotations();
      if (!keyAnnotations.isEmpty()) print(keyAnnotations, true, !commaNeeded);

      if (commaNeeded) l.brk(1, -l.getDefaultIndentation());
      l.end().print("]");
    }
    l.print("(").brk();
    print(itemsProjection, 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  public void print(@NotNull String prefix, @NotNull FP fieldProjection) throws E {
    if (isPrintoutEmpty(fieldProjection)) {
      l.print(prefix);
    } else {
      boolean isBlock = isBlockProjection(fieldProjection);

      if (!isBlock) l.beginIInd(0);
      l.print(prefix);

//      if (isBlock) l.print(" ");
//      else l.brk();
      l.print(" ");

      print(fieldProjection);
      if (!isBlock) l.end();
    }
  }

  @Override
  protected boolean isPrintoutEmpty(@NotNull VP vp) {

    if (!super.isPrintoutEmpty(vp)) return false;

    for (TP tagProjection : vp.tagProjections().values()) {
      final MP modelProjection = tagProjection.projection();
      if (!modelProjection.params().isEmpty()) return false;
    }

    return true;
  }

  public boolean isPrintoutEmpty(@NotNull FP fieldProjection) {
    @NotNull VP fieldVarProjection = fieldProjection.varProjection();
    return !isBlockProjection(fieldProjection) && isPrintoutEmpty(fieldVarProjection);
  }

  public boolean isBlockProjection(@NotNull FP fieldProjection) {
    //return !fieldProjection.params().isEmpty() || !fieldProjection.annotations().isEmpty();
    return false;
  }

}
