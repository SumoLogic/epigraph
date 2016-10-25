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
public class ReqPathPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<
    ReqVarPath,
    ReqTagPath,
    ReqModelPath<?, ?>,
    E> {

  public ReqPathPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull String tagName, @NotNull ReqTagPath tp, int pathSteps) throws E {
    ReqModelPath<?, ?> projection = tp.projection();

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
  public void print(@NotNull ReqModelPath<?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqRecordModelPath)
      print((ReqRecordModelPath) mp, pathSteps);
    else if (mp instanceof ReqMapModelPath)
      print((ReqMapModelPath) mp, pathSteps);
  }

  private void print(@NotNull ReqRecordModelPath mp, int pathSteps) throws E {
    Map<String, ReqFieldPathEntry> fieldProjections = mp.fieldProjections();

    if (pathSteps > 0) {
      if (fieldProjections.isEmpty()) return;
      if (fieldProjections.size() > 1) throw new IllegalArgumentException(
          String.format("Encountered %d fields while still having %d path steps", fieldProjections.size(), pathSteps)
      );

      Map.Entry<String, ReqFieldPathEntry> entry = fieldProjections.entrySet().iterator().next();
      l.beginIInd();
      l.print("/").brk();
      print(entry.getKey(), entry.getValue().projection(), decSteps(pathSteps));
      l.end();

    } else {
      throw new IllegalStateException();
    }
  }

  public void print(@NotNull String fieldName, @NotNull ReqFieldPath fieldProjection, int pathSteps)
      throws E {

    @NotNull ReqVarPath fieldVarProjection = fieldProjection.projection();
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

  private void print(ReqMapModelPath mp, int pathSteps) throws E {
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
  public boolean isPrintoutEmpty(@NotNull ReqModelPath<?, ?> mp) {
    if (mp instanceof ReqRecordModelPath) {
      ReqRecordModelPath recordModelProjection = (ReqRecordModelPath) mp;
      Map<String, ReqFieldPathEntry> fieldProjections =
          recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    return !(mp instanceof ReqMapModelPath); // map key always present
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
