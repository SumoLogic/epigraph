package ws.epigraph.projections.op.input;

import de.uka.ilkd.pp.Layouter;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsPrettyPrinter<E extends Exception> extends AbstractProjectionsPrettyPrinter<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?>,
    E> {

  public OpInputProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull String tagName, @NotNull OpInputTagProjectionEntry tp, int pathSteps) throws E {
    OpInputModelProjection<?, ?, ?> projection = tp.projection();
    OpInputModelProjection<?, ?, ?> metaProjection = projection.metaProjection();
    Annotations annotations = projection.annotations();

    if (projection.defaultValue() == null && annotations.isEmpty() &&
        metaProjection == null) {

      l.beginCInd();
      if (projection.required()) l.print("+");
      l.print(tagName);

      if (!isPrintoutEmpty(projection)) {
        l.brk();
        print(projection, pathSteps);
      }
      l.end();
    } else {
      l.beginCInd();
      if (projection.required()) l.print("+");
      l.print(tagName);
      l.print(" {");

      if (projection.defaultValue() != null) {
        l.brk().beginIInd(0).print("default:").brk();
        dataPrinter.print(projection.defaultValue());
        l.end();
      }

      if (metaProjection != null) {
        l.brk().beginIInd(0).print("meta:").brk();
        print(metaProjection, 0);
        l.end();
      }

      if (!annotations.isEmpty()) print(annotations);

      if (!isPrintoutEmpty(projection)) {
        l.brk();
        print(projection, pathSteps);
      }

      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    }
  }

  @Override
  public void print(@NotNull OpInputModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof OpInputRecordModelProjection)
      print((OpInputRecordModelProjection) mp, pathSteps);
    else if (mp instanceof OpInputMapModelProjection)
      print((OpInputMapModelProjection) mp, pathSteps);
    else if (mp instanceof OpInputListModelProjection)
      print((OpInputListModelProjection) mp, pathSteps);
  }

  private void print(@NotNull OpInputRecordModelProjection mp, int pathSteps) throws E {
    Map<String, OpInputFieldProjectionEntry> fieldProjections = mp.fieldProjections();

    if (pathSteps > 0) {
      if (fieldProjections.isEmpty()) return;
      if (fieldProjections.size() > 1) throw new IllegalArgumentException(
          String.format("Encountered %d fields while still having %d path steps", fieldProjections.size(), pathSteps)
      );

      Map.Entry<String, OpInputFieldProjectionEntry> entry = fieldProjections.entrySet().iterator().next();
      l.beginIInd();
      l.print("/").brk();
      print(entry.getKey(), entry.getValue().projection(), decSteps(pathSteps));
      l.end();

    } else {

      l.print("(").beginCInd();
      boolean first = true;
      for (Map.Entry<String, OpInputFieldProjectionEntry> entry : fieldProjections.entrySet()) {
        if (first) first = false;
        else l.print(",");
        l.brk();

        print(entry.getKey(), entry.getValue().projection(), 0);

      }
      l.brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  private void print(@NotNull String fieldName, @NotNull OpInputFieldProjection fieldProjection, int pathSteps)
      throws E {
    @NotNull OpInputVarProjection fieldVarProjection = fieldProjection.projection();
    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    if (fieldAnnotations.isEmpty()) {
      l.beginIInd();
      if (fieldProjection.required()) l.print("+");
      l.print(fieldName);
      if (!isPrintoutEmpty(fieldVarProjection)) {
        l.brk();
        print(fieldVarProjection, pathSteps);
      }
      l.end();
    } else {
      l.beginCInd();
      if (fieldProjection.required()) l.print("+");
      l.print(fieldName);
      l.print(" {");
      print(fieldAnnotations);
      if (!isPrintoutEmpty(fieldVarProjection)) {
        l.brk();
        print(fieldVarProjection, pathSteps);
      }
      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    }
  }

  private void print(OpInputMapModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered map projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("[](").brk();
    print(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  private void print(OpInputListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(").brk();
    print(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }


  @Override
  public boolean isPrintoutEmpty(@NotNull OpInputModelProjection<?, ?, ?> mp) {
    if (mp instanceof OpInputRecordModelProjection) {
      OpInputRecordModelProjection recordModelProjection = (OpInputRecordModelProjection) mp;
      Map<String, OpInputFieldProjectionEntry> fieldProjections = recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    if (mp instanceof OpInputMapModelProjection) {
      OpInputMapModelProjection mapModelProjection = (OpInputMapModelProjection) mp;
      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    if (mp instanceof OpInputListModelProjection) {
      OpInputListModelProjection inputListModelProjection = (OpInputListModelProjection) mp;
      return isPrintoutEmpty(inputListModelProjection.itemsProjection());
    }

    return true;
  }
}
