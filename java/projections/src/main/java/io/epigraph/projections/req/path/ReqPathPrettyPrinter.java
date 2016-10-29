package io.epigraph.projections.req.path;

import de.uka.ilkd.pp.Layouter;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import io.epigraph.projections.req.ReqParam;
import io.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  protected void printVarOnly(@NotNull ReqVarPath p, int pathSteps) throws E {
    // no tags = end of path
    if (!p.tagProjections().isEmpty()) {
      super.printVarOnly(p, pathSteps);
    }
  }

  @Override
  public void print(@NotNull String tagName, @NotNull ReqTagPath tp, int pathSteps) throws E {
    ReqModelPath<?, ?> projection = tp.projection();

    ReqParams params = projection.params();
    Annotations annotations = projection.annotations();

    l.beginCInd();
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
      print((ReqRecordModelPath) mp);
    else if (mp instanceof ReqMapModelPath)
      print((ReqMapModelPath) mp);
  }

  private void print(@NotNull ReqRecordModelPath mp) throws E {
    @Nullable final ReqFieldPathEntry fieldProjectionEntry = mp.pathFieldProjection();

    if (fieldProjectionEntry != null) {
      l.beginIInd();
      l.print("/").brk();
      print(fieldProjectionEntry.field().name(), fieldProjectionEntry.projection());
      l.end();
    }
  }

  public void print(@NotNull String fieldName, @NotNull ReqFieldPath fieldProjection) throws E {

    @NotNull ReqVarPath fieldVarProjection = fieldProjection.projection();
    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    l.beginIInd();
    l.print(fieldName);

    printParams(fieldProjection.reqParams());
    printAnnotations(fieldAnnotations);

    if (!isPrintoutEmpty(fieldVarProjection)) {
      l.brk();
      print(fieldVarProjection, 1);
    }
    l.end();
  }

  private void print(ReqMapModelPath mp) throws E {
    @NotNull final ReqPathKeyProjection key = mp.key();

    l.beginIInd();
    l.print("/").brk();

    dataPrinter.print(key.value());
    printParams(key.params());
    printAnnotations(key.annotations());

    l.brk();
    print(mp.itemsProjection(), 1);
    l.end();
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
    if (!params.isEmpty()) {
      for (ReqParam param : params.params().values()) {
        l.brk().beginIInd();
        l.print(";").print(param.name()).brk().print("=").brk();
        dataPrinter.print(param.value());
        l.end();
      }
    }
  }

  @Override
  protected boolean isPrintoutEmpty(@NotNull ReqVarPath varPath) {
    // no tags = end of path = empty printout
    return varPath.tagProjections().isEmpty() || super.isPrintoutEmpty(varPath);
  }

  private void printAnnotations(@NotNull Annotations annotations) throws E {
    if (!annotations.isEmpty()) {
      for (Annotation annotation : annotations.params().values()) {
        l.brk().beginIInd();
        l.print("!").print(annotation.name()).brk().print("=").brk();
        gdataPrettyPrinter.print(annotation.value());
        l.end();
      }
    }
  }
}
