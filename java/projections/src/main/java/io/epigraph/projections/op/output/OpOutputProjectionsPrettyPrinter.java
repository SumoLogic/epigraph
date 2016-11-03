package io.epigraph.projections.op.output;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.AbstractOpProjectionsPrettyPrinter;
import io.epigraph.projections.op.OpParam;
import io.epigraph.projections.op.OpParams;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsPrettyPrinter<E extends Exception>
    extends AbstractOpProjectionsPrettyPrinter<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?>,
    OpOutputFieldProjection,
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

      print(fieldName, fieldProjection);
    }
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  public void print(@NotNull OpOutputFieldProjection fieldProjection) throws E {
    @NotNull OpOutputVarProjection fieldVarProjection = fieldProjection.projection();
    @NotNull OpParams fieldParams = fieldProjection.params();
    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    if (fieldParams.isEmpty() && fieldAnnotations.isEmpty()) {
      if (!isPrintoutEmpty(fieldVarProjection)) {
        print(fieldVarProjection, 0);
      }
    } else {
      l.beginCInd();
      l.print("{");
      if (!fieldParams.isEmpty()) print(fieldParams);
      if (!fieldAnnotations.isEmpty()) print(fieldAnnotations);
      l.brk(1, -l.getDefaultIndentation()).end().print("}");
      if (!isPrintoutEmpty(fieldVarProjection)) {
        l.beginIInd();
        l.brk();
        print(fieldVarProjection, 0);
        l.end();
      }
    }

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

      @NotNull OpParams keyParams = keyProjection.params();
      if (!keyParams.isEmpty()) {
        print(keyParams, true, !commaNeeded);
        commaNeeded = !keyParams.isEmpty();
      }

      @NotNull Annotations keyAnnotations = keyProjection.annotations();
      if (!keyAnnotations.isEmpty()) print(keyAnnotations, true, !commaNeeded);

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
    for (OpParam param : p.params().values()) {
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
