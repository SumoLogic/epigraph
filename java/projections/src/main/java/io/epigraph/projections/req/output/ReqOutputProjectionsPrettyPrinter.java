package io.epigraph.projections.req.output;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import io.epigraph.projections.req.ReqParam;
import io.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<ReqOutputVarProjection, ReqOutputTagProjectionEntry, ReqOutputModelProjection<?>, E> {

  // todo: take var projection's 'parenthesized' into account

  public ReqOutputProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull String tagName, @NotNull ReqOutputTagProjectionEntry tp, int pathSteps) throws E {
    ReqOutputModelProjection<?> projection = tp.projection();
    ReqOutputModelProjection<?> metaProjection = projection.metaProjection(); // todo print meta projection

    ReqParams params = projection.params();
    Annotations annotations = projection.annotations();

    l.beginCInd();
    if (projection.required()) l.print("+");
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
  public void print(@NotNull ReqOutputModelProjection<?> mp, int pathSteps) throws E {
    if (mp instanceof ReqOutputRecordModelProjection)
      print((ReqOutputRecordModelProjection) mp, pathSteps);
    else if (mp instanceof ReqOutputMapModelProjection)
      print((ReqOutputMapModelProjection) mp, pathSteps);
    else if (mp instanceof ReqOutputListModelProjection)
      print((ReqOutputListModelProjection) mp, pathSteps);
  }

  private void print(@NotNull ReqOutputRecordModelProjection mp, int pathSteps) throws E {
    @Nullable LinkedHashMap<String, ReqOutputFieldProjectionEntry> fieldProjections = mp.fieldProjections();

    if (fieldProjections != null) {
      if (pathSteps > 0) {
        if (fieldProjections.isEmpty()) return;
        if (fieldProjections.size() > 1) throw new IllegalArgumentException(
            String.format("Encountered %d fields while still having %d path steps", fieldProjections.size(), pathSteps)
        );

        Map.Entry<String, ReqOutputFieldProjectionEntry> entry = fieldProjections.entrySet().iterator().next();
        l.beginIInd();
        l.print("/").brk();
        print(entry.getKey(), entry.getValue().projection(), decSteps(pathSteps));
        l.end();

      } else {

        l.print("(").beginCInd();
        boolean first = true;
        for (Map.Entry<String, ReqOutputFieldProjectionEntry> entry : fieldProjections.entrySet()) {
          if (first) first = false;
          else l.print(",");
          l.brk();

          print(entry.getKey(), entry.getValue().projection(), 0);

        }
        l.brk(1, -l.getDefaultIndentation()).end().print(")");
      }
    }
  }

  public void print(@NotNull String fieldName, @NotNull ReqOutputFieldProjection fieldProjection, int pathSteps)
      throws E {

    @NotNull ReqOutputVarProjection fieldVarProjection = fieldProjection.projection();
    @Nullable Annotations fieldAnnotations = fieldProjection.annotations();

    l.beginIInd();
    if (fieldProjection.required()) l.print("+");
    l.print(fieldName);

    printParams(fieldProjection.reqParams());
    printAnnotations(fieldAnnotations);

    if (!isPrintoutEmpty(fieldVarProjection)) {
      l.brk();
      print(fieldVarProjection, pathSteps);
    }
    l.end();
  }

  private void print(ReqOutputMapModelProjection mp, int pathSteps) throws E {
    @Nullable List<ReqOutputKeyProjection> keys = mp.keys();

    if (pathSteps > 0) {
      l.beginIInd();
      l.print("/").brk();

      if (mp.keysRequired()) l.print("+");

      if (keys != null && keys.size() == 1) {
        ReqOutputKeyProjection key = keys.iterator().next();
        dataPrinter.print(key.value());
        printParams(key.params());
        printAnnotations(key.annotations());
      } else
        throw new IllegalArgumentException(
            String.format("Encountered map projection with %s keys while still having %d path steps",
                          keys == null ? "*" : Integer.valueOf(keys.size()),
                          pathSteps
            )
        );

      l.brk();
      print(mp.itemsProjection(), decSteps(pathSteps));
      l.end();
    } else {
      l.beginIInd();
      if (mp.keysRequired()) l.print("+");
      l.print("[").brk();

      if (keys == null) {
        l.print("*");
      } else {
        boolean first = true;
        for (ReqOutputKeyProjection key : keys) {
          if (first) first = false;
          else l.print(", ");

          dataPrinter.print(key.value());
          printParams(key.params());
          printAnnotations(key.annotations());
        }
      }

      l.brk().print("](");

      if (!isPrintoutEmpty(mp.itemsProjection())) {
        l.brk();
        print(mp.itemsProjection(), 0);
      }
      l.brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  private void print(ReqOutputListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(").brk();
    print(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }


  @Override
  public boolean isPrintoutEmpty(@NotNull ReqOutputModelProjection<?> mp) {
    if (mp instanceof ReqOutputRecordModelProjection) {
      ReqOutputRecordModelProjection recordModelProjection = (ReqOutputRecordModelProjection) mp;
      @Nullable LinkedHashMap<String, ReqOutputFieldProjectionEntry> fieldProjections =
          recordModelProjection.fieldProjections();
      return fieldProjections == null || fieldProjections.isEmpty();
    }

    if (mp instanceof ReqOutputMapModelProjection) {
      ReqOutputMapModelProjection mapModelProjection = (ReqOutputMapModelProjection) mp;
      @Nullable List<ReqOutputKeyProjection> keys = mapModelProjection.keys();
      return keys == null && isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    if (mp instanceof ReqOutputListModelProjection) {
      ReqOutputListModelProjection inputListModelProjection = (ReqOutputListModelProjection) mp;
      return isPrintoutEmpty(inputListModelProjection.itemsProjection());
    }

    return true;
  }

  private void printParams(@Nullable ReqParams params) throws E { // move to req common?
    l.beginCInd();
    if (params != null) {
      for (ReqParam param : params.params().values()) {
        l.brk().beginIInd();
        l.print(";").print(param.name()).brk().print("=").brk();
        dataPrinter.print(param.value());
        l.end();
      }
    }
    l.end();
  }

  private void printAnnotations(@Nullable Annotations annotations) throws E {
    l.beginCInd();
    if (annotations != null) {
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
