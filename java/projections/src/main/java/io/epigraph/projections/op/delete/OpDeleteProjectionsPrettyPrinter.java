package io.epigraph.projections.op.delete;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.data.Datum;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import io.epigraph.projections.op.OpParam;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.op.input.OpInputProjectionsPrettyPrinter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteProjectionsPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?>,
    E> {

  public OpDeleteProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  // todo override/augment print(VP) to show `canDelete`; keep in sync with grammar

  @Override
  public void print(@NotNull String tagName, @NotNull OpDeleteTagProjectionEntry tp, int pathSteps) throws E {
    OpDeleteModelProjection<?, ?> projection = tp.projection();
    OpDeleteModelProjection<?, ?> metaProjection = projection.metaProjection();
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

      if (!isPrintoutEmpty(projection)) {
        l.brk();
        print(projection, 0);
      }

      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    }
  }

  @Override
  public void print(@NotNull OpDeleteModelProjection<?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpDeleteRecordModelProjection)
      print((OpDeleteRecordModelProjection) mp);
    else if (mp instanceof OpDeleteMapModelProjection)
      print((OpDeleteMapModelProjection) mp);
    else if (mp instanceof OpDeleteListModelProjection)
      print((OpDeleteListModelProjection) mp);
  }

  private void print(@NotNull OpDeleteRecordModelProjection mp) throws E {
    Map<String, OpDeleteFieldProjectionEntry> fieldProjections = mp.fieldProjections();

    l.print("(").beginCInd();
    boolean first = true;
    for (Map.Entry<String, OpDeleteFieldProjectionEntry> entry : fieldProjections.entrySet()) {
      if (first) first = false;
      else l.print(",");
      l.brk();

      @NotNull String fieldName = entry.getKey();
      @NotNull OpDeleteFieldProjection fieldProjection = entry.getValue().projection();
      @NotNull OpDeleteVarProjection fieldVarProjection = fieldProjection.projection();
      @NotNull OpParams fieldParams = fieldProjection.params();
      @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

      if (fieldParams.isEmpty() && fieldAnnotations.isEmpty()) {
        l.beginIInd();
        l.print(fieldName);
        if (!isPrintoutEmpty(fieldVarProjection)) {
          l.brk();
          print(fieldVarProjection, 0);
        }
        l.end();
      } else {
        l.beginCInd();
        l.print(fieldName);
        l.print(" {");
        if (!fieldParams.isEmpty()) print(fieldParams);
        if (!fieldAnnotations.isEmpty()) print(fieldAnnotations);
        if (!isPrintoutEmpty(fieldVarProjection)) {
          l.brk();
          print(fieldVarProjection, 0);
        }
        l.brk(1, -l.getDefaultIndentation()).end().print("}");
      }

    }
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  private void print(OpDeleteMapModelProjection mp) throws E {
    l.beginIInd();

    { // keys
      @NotNull OpDeleteKeyProjection keyProjection = mp.keyProjection();

      l.beginCInd();
      l.print("[");
      boolean commaNeeded = false;

      if (keyProjection.presence() == OpDeleteKeyProjection.Presence.FORBIDDEN) {
        l.brk().print("forbidden");
        commaNeeded = true;
      }

      if (keyProjection.presence() == OpDeleteKeyProjection.Presence.REQUIRED) {
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

  private void print(OpDeleteListModelProjection mp) throws E {
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
    if (!annotations.isEmpty()) {
      l.beginCInd();
      l.print(" {");
      print(annotations);
      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    }

    l.end();
  }

  @Override
  public boolean isPrintoutEmpty(@NotNull OpDeleteModelProjection<?, ?> mp) {
    if (mp instanceof OpDeleteRecordModelProjection) {
      OpDeleteRecordModelProjection recordModelProjection = (OpDeleteRecordModelProjection) mp;
      Map<String, OpDeleteFieldProjectionEntry> fieldProjections = recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    if (mp instanceof OpDeleteMapModelProjection) {
      OpDeleteMapModelProjection mapModelProjection = (OpDeleteMapModelProjection) mp;
      @NotNull OpDeleteKeyProjection keyProjection = mapModelProjection.keyProjection();

      if (keyProjection.presence() != OpDeleteKeyProjection.Presence.OPTIONAL) return false;
      if (!keyProjection.params().isEmpty()) return false;
      if (!keyProjection.annotations().isEmpty()) return false;

      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    if (mp instanceof OpDeleteListModelProjection) {
      OpDeleteListModelProjection deleteListModelProjection = (OpDeleteListModelProjection) mp;
      return isPrintoutEmpty(deleteListModelProjection.itemsProjection());
    }

    return true;
  }
}
