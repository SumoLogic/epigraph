package io.epigraph.projections.op.input;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.projections.generic.GenericProjectionsPrettyPrinter;
import io.epigraph.projections.op.OpCustomParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsPrettyPrinter<E extends Exception>
    extends GenericProjectionsPrettyPrinter<OpInputVarProjection, OpInputTagProjection, OpInputModelProjection<?, ?>, E> {

  public OpInputProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull OpInputTagProjection tp) throws E {
    OpInputModelProjection<?, ?> projection = tp.projection();
    OpInputModelProjection<?, ?> metaProjection = projection.metaProjection();
    OpCustomParams customParams = projection.customParams();

    if (projection.defaultValue() == null && customParams == null &&
        metaProjection == null) {

      l.beginCInd();
      if (projection.required()) l.print("+");
      l.print(tp.tag().name());

      if (!isPrintoutEmpty(projection)) {
        l.brk();
        print(projection);
      }
      l.end();
    } else {
      l.beginCInd();
      if (projection.required()) l.print("+");
      l.print(tp.tag().name());
      l.print(" {");

      if (projection.defaultValue() != null) {
        l.brk().beginIInd(0).print("default:").brk();
        dataPrinter.print(projection.defaultValue());
        l.end();
      }

      if (metaProjection != null) {
        l.brk().beginIInd(0).print("meta:").brk();
        print(metaProjection);
        l.end();
      }

      if (customParams != null) print(customParams);

      if (!isPrintoutEmpty(projection)) {
        l.brk();
        print(projection);
      }

      l.brk(1, -l.getDefaultIndentation()).end().print("}");
    }
  }

  @Override
  public void print(@NotNull OpInputModelProjection<?, ?> mp) throws E {
    if (mp instanceof OpInputRecordModelProjection)
      print((OpInputRecordModelProjection) mp);
    else if (mp instanceof OpInputMapModelProjection)
      print((OpInputMapModelProjection) mp);
    else if (mp instanceof OpInputListModelProjection)
      print((OpInputListModelProjection) mp);
  }

  private void print(@NotNull OpInputRecordModelProjection mp) throws E {
    @Nullable LinkedHashSet<OpInputFieldProjection> fieldProjections = mp.fieldProjections();

    if (fieldProjections != null) {
      l.print("(").beginCInd();
      boolean first = true;
      for (OpInputFieldProjection fieldProjection : fieldProjections) {
        if (first) first = false;
        else l.print(",");
        l.brk();

        @NotNull OpInputVarProjection fieldVarProjection = fieldProjection.projection();
        @Nullable OpCustomParams fieldCustomParams = fieldProjection.customParams();

        if (fieldCustomParams == null) {
          l.beginIInd();
          if (fieldProjection.required()) l.print("+");
          l.print(fieldProjection.field().name());
          if (!isPrintoutEmpty(fieldVarProjection)) {
            l.brk();
            print(fieldVarProjection);
          }
          l.end();
        } else {
          l.beginCInd();
          if (fieldProjection.required()) l.print("+");
          l.print(fieldProjection.field().name());
          l.print(" {");
          print(fieldCustomParams);
          if (!isPrintoutEmpty(fieldVarProjection)) {
            l.brk();
            print(fieldVarProjection);
          }
          l.brk(1, -l.getDefaultIndentation()).end().print("}");
        }

      }
      l.brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  private void print(OpInputMapModelProjection mp) throws E {
    l.beginIInd();
    l.print("[](").brk();
    print(mp.itemsProjection());
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  private void print(OpInputListModelProjection mp) throws E {
    l.beginIInd();
    l.print("*(").brk();
    print(mp.itemsProjection());
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }


  @Override
  public boolean isPrintoutEmpty(@NotNull OpInputModelProjection<?, ?> mp) {
    if (mp instanceof OpInputRecordModelProjection) {
      OpInputRecordModelProjection recordModelProjection = (OpInputRecordModelProjection) mp;
      @Nullable LinkedHashSet<OpInputFieldProjection> fieldProjections =
          recordModelProjection.fieldProjections();
      return fieldProjections == null || fieldProjections.isEmpty();
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
