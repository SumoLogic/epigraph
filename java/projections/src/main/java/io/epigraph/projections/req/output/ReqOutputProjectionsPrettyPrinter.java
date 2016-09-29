package io.epigraph.projections.req.output;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.generic.GenericProjectionsPrettyPrinter;
import io.epigraph.projections.req.ReqParam;
import io.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsPrettyPrinter<E extends Exception>
    extends GenericProjectionsPrettyPrinter<ReqOutputVarProjection, ReqOutputTagProjection, ReqOutputModelProjection<?>, E> {

  public ReqOutputProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull ReqOutputTagProjection tp, int pathSteps) throws E {
    ReqOutputModelProjection<?> projection = tp.projection();
    ReqOutputModelProjection<?> metaProjection = projection.metaProjection();

    ReqParams params = projection.params();
    Annotations annotations = projection.annotations();

    l.beginCInd();
    if (projection.required()) l.print("+");
    l.print(tp.tag().name());

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
    @Nullable LinkedHashSet<ReqOutputFieldProjection> fieldProjections = mp.fieldProjections();

    if (fieldProjections != null) {
      if (pathSteps > 0) {
        if (fieldProjections.isEmpty()) return;
        if (fieldProjections.size() > 1) throw new IllegalArgumentException(
            String.format("Encountered %d fields while still having %d path steps", fieldProjections.size(), pathSteps)
        );

        ReqOutputFieldProjection fieldProjection = fieldProjections.iterator().next();
        l.beginIInd();
        l.print("/").brk();
        print(fieldProjection, decSteps(pathSteps));
        l.end();

      } else {

        l.print("(").beginCInd();
        boolean first = true;
        for (ReqOutputFieldProjection fieldProjection : fieldProjections) {
          if (first) first = false;
          else l.print(",");
          l.brk();

          print(fieldProjection, 0);

        }
        l.brk(1, -l.getDefaultIndentation()).end().print(")");
      }
    }
  }

  private void print(ReqOutputFieldProjection fieldProjection, int pathSteps) throws E {
    @NotNull ReqOutputVarProjection fieldVarProjection = fieldProjection.projection();
    @Nullable Annotations fieldAnnotations = fieldProjection.annotations();

    l.beginIInd();
    if (fieldProjection.required()) l.print("+");
    l.print(fieldProjection.field().name());

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
      @Nullable LinkedHashSet<ReqOutputFieldProjection> fieldProjections =
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
