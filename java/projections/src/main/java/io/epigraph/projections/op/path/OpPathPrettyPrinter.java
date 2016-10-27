package io.epigraph.projections.op.path;

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

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPathPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<
    OpVarPath,
    OpTagPath,
    OpModelPath<?, ?>,
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
  public void print(@NotNull String tagName, @NotNull OpTagPath tp, int pathSteps) throws E {
    OpModelPath<?, ?> projection = tp.projection();
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

      if (!isPrintoutEmpty(projection)) {
        l.brk();
        print(projection, 0);
      }

      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    }
  }

  @Override
  public void print(@NotNull OpModelPath<?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpRecordModelPath)
      print((OpRecordModelPath) mp);
    else if (mp instanceof OpMapModelPath)
      print((OpMapModelPath) mp);
  }

  private void print(@NotNull OpRecordModelPath mp) throws E {
    @Nullable final OpFieldPathEntry entry = mp.pathFieldProjection();

    if (entry != null) {
      l.beginIInd();
      l.print("/").brk();

      @NotNull String fieldName = entry.field().name();
      @NotNull OpFieldPath fieldProjection = entry.projection();

      @NotNull OpVarPath fieldVarProjection = fieldProjection.projection();
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
  protected boolean isPrintoutEmpty(@NotNull OpVarPath opVarPath) {
    // no tags = end of path = empty printout
    return opVarPath.tagProjections().isEmpty() || super.isPrintoutEmpty(opVarPath);
  }

  @Override
  public boolean isPrintoutEmpty(@NotNull OpModelPath<?, ?> mp) {
    if (mp instanceof OpRecordModelPath) {
      OpRecordModelPath recordModelProjection = (OpRecordModelPath) mp;
      return recordModelProjection.pathFieldProjection() == null;
    }

    if (mp instanceof OpMapModelPath) {
      OpMapModelPath mapModelProjection = (OpMapModelPath) mp;
      @NotNull OpPathKeyProjection keyProjection = mapModelProjection.keyProjection();

      if (!keyProjection.params().isEmpty()) return false;
      if (!keyProjection.annotations().isEmpty()) return false;

      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    return true;
  }
}
