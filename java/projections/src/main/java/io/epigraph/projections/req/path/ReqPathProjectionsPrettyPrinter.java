package io.epigraph.projections.req.path;

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
public class ReqPathProjectionsPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<
    ReqPathVarProjection,
    ReqPathTagProjectionEntry,
    ReqPathModelProjection<?, ?>,
    E> {

  public ReqPathProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull String tagName, @NotNull ReqPathTagProjectionEntry tp, int pathSteps) throws E {
    ReqPathModelProjection<?, ?> projection = tp.projection();

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
  public void print(@NotNull ReqPathModelProjection<?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqPathRecordModelProjection)
      print((ReqPathRecordModelProjection) mp, pathSteps);
    else if (mp instanceof ReqPathMapModelProjection)
      print((ReqPathMapModelProjection) mp, pathSteps);
  }

  private void print(@NotNull ReqPathRecordModelProjection mp, int pathSteps) throws E {
    Map<String, ReqPathFieldProjectionEntry> fieldProjections = mp.fieldProjections();

    if (pathSteps > 0) {
      if (fieldProjections.isEmpty()) return;
      if (fieldProjections.size() > 1) throw new IllegalArgumentException(
          String.format("Encountered %d fields while still having %d path steps", fieldProjections.size(), pathSteps)
      );

      Map.Entry<String, ReqPathFieldProjectionEntry> entry = fieldProjections.entrySet().iterator().next();
      l.beginIInd();
      l.print("/").brk();
      print(entry.getKey(), entry.getValue().projection(), decSteps(pathSteps));
      l.end();

    } else {
      throw new IllegalStateException();
    }
  }

  public void print(@NotNull String fieldName, @NotNull ReqPathFieldProjection fieldProjection, int pathSteps)
      throws E {

    @NotNull ReqPathVarProjection fieldVarProjection = fieldProjection.projection();
    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

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

  private void print(ReqPathMapModelProjection mp, int pathSteps) throws E {
    @NotNull final ReqPathKeyProjection key = mp.key();

    if (pathSteps > 0) {
      l.beginIInd();
      l.print("/").brk();

      dataPrinter.print(key.value());
      printParams(key.params());
      printAnnotations(key.annotations());

      l.brk();
      print(mp.itemsProjection(), decSteps(pathSteps));
      l.end();
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public boolean isPrintoutEmpty(@NotNull ReqPathModelProjection<?, ?> mp) {
    if (mp instanceof ReqPathRecordModelProjection) {
      ReqPathRecordModelProjection recordModelProjection = (ReqPathRecordModelProjection) mp;
      Map<String, ReqPathFieldProjectionEntry> fieldProjections =
          recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    return !(mp instanceof ReqPathMapModelProjection); // map key always present
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
