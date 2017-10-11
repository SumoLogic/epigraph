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

package ws.epigraph.projections.req;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathPrettyPrinter<E extends Exception>
    extends AbstractReqPrettyPrinter<ReqEntityProjection, ReqTagProjectionEntry, ReqModelProjection<?, ?, ?>, E> {

  public ReqPathPrettyPrinter(Layouter<E> layouter) {
    super(layouter, new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.EMPTY, null));
  }

  @Override
  protected void printEntityOnly(@NotNull ReqEntityProjection p, int pathSteps) throws E {
    // no tags = end of path
    if (!p.tagProjections().isEmpty()) {
      super.printEntityOnly(p, pathSteps);
    }
  }

  @Override
  protected boolean printModelParams(final @NotNull ReqModelProjection<?, ?, ?> projection) throws E {
    ReqParams params = projection.params();
    Directives directives = projection.directives();

    l.beginIInd(0);
    boolean empty = true;

    if (!params.isEmpty()) {
      printParams(params);
      empty = false;
    }

    if (!directives.isEmpty()) {
      printDirectives(directives);
      empty = false;
    }

    l.end();

    return empty;
  }

  @Override
  public void printModelOnly(@NotNull ReqModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqRecordModelProjection)
      printModelOnly((ReqRecordModelProjection) mp);
    else if (mp instanceof ReqMapModelProjection)
      printModelOnly((ReqMapModelProjection) mp);
  }

  private void printModelOnly(@NotNull ReqRecordModelProjection mp) throws E {
    final @Nullable ReqFieldProjectionEntry fieldProjectionEntry = mp.pathFieldProjection();

    if (fieldProjectionEntry != null) {
      l.beginIInd();
      l.print("/");
      brk();
      print(fieldProjectionEntry.field().name(), fieldProjectionEntry.fieldProjection());
      l.end();
    }
  }

  public void print(@NotNull String fieldName, @NotNull ReqFieldProjection fieldProjection) throws E {

    @NotNull ReqEntityProjection fieldVarProjection = fieldProjection.entityProjection();
//    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    l.beginIInd();
    l.print(fieldName);

//    printParams(fieldProjection.params());
//    printAnnotations(fieldAnnotations);

    if (!isPrintoutEmpty(fieldVarProjection)) {
      brk(); // FIXME don't need a break if model parameters will be printed next
      printEntity(fieldVarProjection, 1);
    }
    l.end();
  }

  private void printModelOnly(ReqMapModelProjection mp) throws E {
    final @NotNull ReqKeyProjection key = mp.pathKey();

    l.beginIInd();
    l.print("/");
    brk();

    printReqKey(key, true);

    if (!isPrintoutEmpty(mp.itemsProjection()))
      brk();

    printEntity(mp.itemsProjection(), 1);
    l.end();
  }

  @Override
  public boolean isPrintoutNoParamsEmpty(@NotNull ReqModelProjection<?, ?, ?> mp) {
    if (mp instanceof ReqRecordModelProjection)
      return super.isPrintoutNoParamsEmpty(mp);

    return !(mp instanceof ReqMapModelProjection); // map key always present
  }

  @Override
  public boolean modelParamsEmpty(final @NotNull ReqModelProjection<?, ?, ?> path) {
    return super.modelParamsEmpty(path) && path.directives().isEmpty() && path.params().isEmpty();
  }

  @Override
  protected boolean isPrintoutEmpty(@NotNull ReqEntityProjection varPath) {
    // no tags = end of path = empty printout
    if (varPath.tagProjections().isEmpty()) return true;
    if (!super.isPrintoutEmpty(varPath)) return false;
    //noinspection ConstantConditions
    ReqModelProjection<?, ?, ?> modelPath = varPath.singleTagProjection().projection();
    return modelPath.params().isEmpty() && modelPath.directives().isEmpty();
  }

}
