/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.projections.req.path;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.projections.req.Directive;
import ws.epigraph.projections.req.Directives;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.ReqParam;
import ws.epigraph.projections.req.ReqParams;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<ReqVarPath, ReqTagPath, ReqModelPath<?, ?, ?>, E> {

  protected @NotNull DataPrinter<E> dataPrinter;

  public ReqPathPrettyPrinter(Layouter<E> layouter) {
    super(layouter, new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.EMPTY, null));
    dataPrinter = new DataPrinter<>(layouter);
  }

  @Override
  protected void printVarOnly(@NotNull ReqVarPath p, int pathSteps) throws E {
    // no tags = end of path
    if (!p.tagProjections().isEmpty()) {
      super.printVarOnly(p, pathSteps);
    }
  }

  @Override
  protected boolean printModelParams(final @NotNull ReqModelPath<?, ?, ?> projection) throws E {
    ReqParams params = projection.params();
    Directives directives = projection.directives();

    l.beginIInd(0);
    boolean empty = true;

    if (!params.isEmpty()) {
      printParams(params);
      empty = false;
    }

    if (!directives.isEmpty()) {
      printAnnotations(directives);
      empty = false;
    }

    l.end();

    return empty;
  }

  @Override
  public void printModelOnly(@NotNull ReqModelPath<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqRecordModelPath)
      printModelOnly((ReqRecordModelPath) mp);
    else if (mp instanceof ReqMapModelPath)
      printModelOnly((ReqMapModelPath) mp);
  }

  private void printModelOnly(@NotNull ReqRecordModelPath mp) throws E {
    final @Nullable ReqFieldPathEntry fieldProjectionEntry = mp.pathFieldProjection();

    if (fieldProjectionEntry != null) {
      l.beginIInd();
      l.print("/");
      brk();
      print(fieldProjectionEntry.field().name(), fieldProjectionEntry.fieldProjection());
      l.end();
    }
  }

  public void print(@NotNull String fieldName, @NotNull ReqFieldPath fieldProjection) throws E {

    @NotNull ReqVarPath fieldVarProjection = fieldProjection.varProjection();
//    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    l.beginIInd();
    l.print(fieldName);

//    printParams(fieldProjection.params());
//    printAnnotations(fieldAnnotations);

    if (!isPrintoutEmpty(fieldVarProjection)) {
      brk(); // FIXME don't need a break if model parameters will be printed next
      printVar(fieldVarProjection, 1);
    }
    l.end();
  }

  private void printModelOnly(ReqMapModelPath mp) throws E {
    final @NotNull ReqPathKeyProjection key = mp.key();

    l.beginIInd();
    l.print("/");
    brk();

    dataPrinter.print(null, key.value());
    printParams(key.params());
    printAnnotations(key.annotations());

    if (!isPrintoutEmpty(mp.itemsProjection()))
      brk();

    printVar(mp.itemsProjection(), 1);
    l.end();
  }

  @Override
  public boolean isPrintoutNoParamsEmpty(@NotNull ReqModelPath<?, ?, ?> mp) {
    if (mp instanceof ReqRecordModelPath)
      return super.isPrintoutNoParamsEmpty(mp);

    return !(mp instanceof ReqMapModelPath); // map key always present
  }

  private void printParams(@NotNull ReqParams params) throws E { // move to req common?
    if (!params.isEmpty()) {
      for (ReqParam param : params.asMap().values()) {
        l.beginIInd();
        l.print(";").print(param.name());
        brk().print("=");
        brk();
        dataPrinter.print(null, param.value());
        l.end();
      }
    }
  }

  @Override
  public boolean modelParamsEmpty(final @NotNull ReqModelPath<?, ?, ?> path) {
    return super.modelParamsEmpty(path) && path.directives().isEmpty() && path.params().isEmpty();
  }

  @Override
  protected boolean isPrintoutEmpty(@NotNull ReqVarPath varPath) {
    // no tags = end of path = empty printout
    if (varPath.tagProjections().isEmpty()) return true;
    if (!super.isPrintoutEmpty(varPath)) return false;
    //noinspection ConstantConditions
    ReqModelPath<?, ?, ?> modelPath = varPath.singleTagProjection().projection();
    return modelPath.params().isEmpty() && modelPath.directives().isEmpty();
  }

  public void printAnnotations(@NotNull Directives directives) throws E {
    if (!directives.isEmpty()) {
      for (Directive directive : directives.asMap().values()) {
        l.beginIInd();
        l.print("!").print(directive.name());
        brk().print("=");
        brk();
        gdataPrettyPrinter.print(directive.value());
        l.end();
      }
    }
  }
}
