package io.epigraph.projections.req.update;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import io.epigraph.projections.req.ReqParam;
import io.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateProjectionsPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?>,
    E> {

  // todo: take var projection's 'parenthesized' into account

  public ReqUpdateProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull String tagName, @NotNull ReqUpdateTagProjectionEntry tp, int pathSteps) throws E {
    ReqUpdateModelProjection<?, ?> projection = tp.projection();
    ReqUpdateModelProjection<?, ?> metaProjection = projection.metaProjection(); // todo print meta projection

    ReqParams params = projection.params();
    Annotations annotations = projection.annotations();

    l.beginCInd();
    if (projection.update()) l.print("+");
    l.print(tagName);

    printParams(params);
    printAnnotations(annotations);

    if (!isPrintoutEmpty(projection)) {
      l.brk();
      print(projection, pathSteps);
    }
    l.end();
  }

  @Override
  public void print(@NotNull ReqUpdateModelProjection<?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqUpdateRecordModelProjection)
      print((ReqUpdateRecordModelProjection) mp, pathSteps);
    else if (mp instanceof ReqUpdateMapModelProjection)
      print((ReqUpdateMapModelProjection) mp, pathSteps);
    else if (mp instanceof ReqUpdateListModelProjection)
      print((ReqUpdateListModelProjection) mp, pathSteps);
  }

  private void print(@NotNull ReqUpdateRecordModelProjection mp, int pathSteps) throws E {
    Map<String, ReqUpdateFieldProjectionEntry> fieldProjections = mp.fieldProjections();

    if (pathSteps > 0) {
      if (fieldProjections.isEmpty()) return;
      if (fieldProjections.size() > 1) throw new IllegalArgumentException(
          String.format("Encountered %d fields while still having %d path steps", fieldProjections.size(), pathSteps)
      );

      Map.Entry<String, ReqUpdateFieldProjectionEntry> entry = fieldProjections.entrySet().iterator().next();
      l.beginIInd();
      l.print("/").brk();
      print(entry.getKey(), entry.getValue().projection(), decSteps(pathSteps));
      l.end();

    } else {

      l.print("(").beginCInd();
      boolean first = true;
      for (Map.Entry<String, ReqUpdateFieldProjectionEntry> entry : fieldProjections.entrySet()) {
        if (first) first = false;
        else l.print(",");
        l.brk();

        print(entry.getKey(), entry.getValue().projection(), 0);

      }
      l.brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  public void print(@NotNull String fieldName, @NotNull ReqUpdateFieldProjection fieldProjection, int pathSteps)
      throws E {

    @NotNull ReqUpdateVarProjection fieldVarProjection = fieldProjection.projection();
    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    l.beginIInd();
    if (fieldProjection.update()) l.print("+");
    l.print(fieldName);

    printParams(fieldProjection.reqParams());
    printAnnotations(fieldAnnotations);

    if (!isPrintoutEmpty(fieldVarProjection)) {
      l.brk();
      print(fieldVarProjection, pathSteps);
    }
    l.end();
  }

  private void print(ReqUpdateMapModelProjection mp, int pathSteps) throws E {
    @NotNull ReqUpdateKeysProjection keys = mp.keys();

    if (pathSteps > 0) {
      l.beginIInd();
      l.print("/").brk();

      if (keys.update()) l.print("+");
      l.print("*");

      l.brk();
      print(mp.itemsProjection(), decSteps(pathSteps));
      l.end();
    } else {
      l.beginIInd();
      l.print("[").brk();

      if (keys.update()) l.print("+"); // todo check with others if this goes inside or outside of []
      l.print("*");

      l.brk().print("](");

      if (!isPrintoutEmpty(mp.itemsProjection())) {
        l.brk();
        print(mp.itemsProjection(), 0);
      }
      l.brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  private void print(ReqUpdateListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(").brk();
    print(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }


  @Override
  public boolean isPrintoutEmpty(@NotNull ReqUpdateModelProjection<?, ?> mp) {
    if (mp instanceof ReqUpdateRecordModelProjection) {
      ReqUpdateRecordModelProjection recordModelProjection = (ReqUpdateRecordModelProjection) mp;
      Map<String, ReqUpdateFieldProjectionEntry> fieldProjections =
          recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    if (mp instanceof ReqUpdateMapModelProjection) {
      ReqUpdateMapModelProjection mapModelProjection = (ReqUpdateMapModelProjection) mp;
      @NotNull ReqUpdateKeysProjection keys = mapModelProjection.keys();
      return !keys.update() && isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    if (mp instanceof ReqUpdateListModelProjection) {
      ReqUpdateListModelProjection inputListModelProjection = (ReqUpdateListModelProjection) mp;
      return isPrintoutEmpty(inputListModelProjection.itemsProjection());
    }

    return true;
  }

  private void printParams(@NotNull ReqParams params) throws E { // move to req common?
    l.beginCInd();
    if (!params.isEmpty()) {
      for (ReqParam param : params.params().values()) {
        l.brk().beginIInd();
        l.print(";").print(param.name()).brk().print("=").brk();
        dataPrinter.print(param.value());
        l.end();
      }
    }
    l.end();
  }

  private void printAnnotations(@NotNull Annotations annotations) throws E {
    l.beginCInd();
    if (!annotations.isEmpty()) {
      for (Annotation annotation : annotations.params().values()) {
        l.brk().beginIInd();
        l.print("!").print(annotation.name()).brk().print("=").brk();
        gdataPrettyPrinter.print(annotation.value());
        l.end();
      }
    }
    l.end();
  }
}
