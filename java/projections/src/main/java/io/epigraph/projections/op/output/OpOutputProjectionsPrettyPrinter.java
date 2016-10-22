package io.epigraph.projections.op.output;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.data.Datum;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import io.epigraph.projections.op.OpParam;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.op.input.OpInputProjectionsPrettyPrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?>,
    E> {

  public OpOutputProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull String tagName, @NotNull OpOutputTagProjectionEntry tp, int pathSteps) throws E {
    OpOutputModelProjection<?, ?> projection = tp.projection();
    OpOutputModelProjection<?, ?> metaProjection = projection.metaProjection();
    OpParams params = projection.params();
    Annotations annotations = projection.annotations();

    if (params == null && annotations == null) {
      l.beginCInd();
      if (projection.includeInDefault()) l.print("+");
      l.print(tagName);

      if (!isPrintoutEmpty(projection)) {
        l.brk();
        print(projection, 0);
      }

      l.end();
    } else {
      l.beginCInd();
      if (projection.includeInDefault()) l.print("+");
      l.print(tagName);
      l.print(" {");

      if (params != null) print(params);
      if (annotations != null) print(annotations);

      if (metaProjection != null) {
        l.brk().beginIInd(0).print("meta:").brk();
        print(metaProjection, 0);
        l.end();
      }

      if (!isPrintoutEmpty(projection)) {
        l.brk();
        print(projection, 0);
      }

      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    }
  }

  @Override
  public void print(@NotNull OpOutputModelProjection<?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpOutputRecordModelProjection)
      print((OpOutputRecordModelProjection) mp);
    else if (mp instanceof OpOutputMapModelProjection)
      print((OpOutputMapModelProjection) mp);
    else if (mp instanceof OpOutputListModelProjection)
      print((OpOutputListModelProjection) mp);
  }

  private void print(@NotNull OpOutputRecordModelProjection mp) throws E {
    Map<String, OpOutputFieldProjectionEntry> fieldProjections = mp.fieldProjections();

    l.print("(").beginCInd();
    boolean first = true;
    for (Map.Entry<String, OpOutputFieldProjectionEntry> entry : fieldProjections.entrySet()) {
      if (first) first = false;
      else l.print(",");
      l.brk();

      @NotNull String fieldName = entry.getKey();
      @NotNull OpOutputFieldProjection fieldProjection = entry.getValue().projection();
      @NotNull OpOutputVarProjection fieldVarProjection = fieldProjection.projection();
      @Nullable OpParams fieldParams = fieldProjection.params();
      @Nullable Annotations fieldAnnotations = fieldProjection.annotations();

      if (fieldParams == null && fieldAnnotations == null) {
        l.beginIInd();
        if (fieldProjection.includeInDefault()) l.print("+");
        l.print(fieldName);
        if (!isPrintoutEmpty(fieldVarProjection)) {
          l.brk();
          print(fieldVarProjection, 0);
        }
        l.end();
      } else {
        l.beginCInd();
        if (fieldProjection.includeInDefault()) l.print("+");
        l.print(fieldName);
        l.print(" {");
        if (fieldParams != null) print(fieldParams);
        if (fieldAnnotations != null) print(fieldAnnotations);
        if (!isPrintoutEmpty(fieldVarProjection)) {
          l.brk();
          print(fieldVarProjection, 0);
        }
        l.brk(1, -l.getDefaultIndentation()).end().print("}");
      }

    }
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  private void print(OpOutputMapModelProjection mp) throws E {
    l.beginIInd();

    { // keys
      @NotNull OpOutputKeyProjection keyProjection = mp.keyProjection();

      l.beginCInd();
      l.print("[");
      boolean commaNeeded = false;

      if (keyProjection.presence() == OpOutputKeyProjection.Presence.FORBIDDEN) {
        l.brk().print("forbidden");
        commaNeeded = true;
      }

      if (keyProjection.presence() == OpOutputKeyProjection.Presence.REQUIRED) {
        if (commaNeeded) l.print(",");
        l.brk().print("required");
        commaNeeded = true;
      }

      @Nullable OpParams keyParams = keyProjection.params();
      if (keyParams != null) {
        print(keyParams, true, !commaNeeded);
        commaNeeded = !keyParams.isEmpty();
      }

      @Nullable Annotations keyAnnotations = keyProjection.annotations();
      if (keyAnnotations != null) print(keyAnnotations, true, !commaNeeded);

      l.brk(1, -l.getDefaultIndentation()).end().print("]");
    }

    l.print("(").brk();
    print(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  private void print(OpOutputListModelProjection mp) throws E {
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
    for (OpParam param : p.params().values()) {
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

    Datum defaultValue = projection.defaultValue();
    if (defaultValue != null) {
      l.brk().print("=").brk();
      dataPrinter.print(defaultValue);
    }

    Annotations annotations = projection.annotations();
    if (annotations != null) {
      l.beginCInd();
      l.print(" {");
      print(annotations);
      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    }

    l.end();
  }

  @Override
  public boolean isPrintoutEmpty(@NotNull OpOutputModelProjection<?, ?> mp) {
    if (mp instanceof OpOutputRecordModelProjection) {
      OpOutputRecordModelProjection recordModelProjection = (OpOutputRecordModelProjection) mp;
      Map<String, OpOutputFieldProjectionEntry> fieldProjections = recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    if (mp instanceof OpOutputMapModelProjection) {
      OpOutputMapModelProjection mapModelProjection = (OpOutputMapModelProjection) mp;
      @NotNull OpOutputKeyProjection keyProjection = mapModelProjection.keyProjection();

      if (keyProjection.presence() != OpOutputKeyProjection.Presence.OPTIONAL) return false;
      if (keyProjection.params() != null) return false;
      if (keyProjection.annotations() != null) return false;

      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    if (mp instanceof OpOutputListModelProjection) {
      OpOutputListModelProjection outputListModelProjection = (OpOutputListModelProjection) mp;
      return isPrintoutEmpty(outputListModelProjection.itemsProjection());
    }

    return true;
  }
}
