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

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqProjectionsPrettyPrinter<E extends Exception>
    extends AbstractReqProjectionsPrettyPrinter<
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>,
    ReqRecordModelProjection,
    ReqFieldProjectionEntry,
    ReqFieldProjection,
    E> {

  // todo: take var projection's 'parenthesized' into account

  public ReqProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<ReqEntityProjection, ReqModelProjection<?, ?, ?>> context) {
    super(layouter, context);
  }

  public ReqProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.EMPTY, null));
  }

  @Override
  protected void printTagName(
      final @NotNull String tagName,
      final @NotNull ReqModelProjection<?, ?, ?> projection) throws E {
    if (projection.flag()) l.print("+");
    super.printTagName(tagName, projection);
  }

  @Override
  protected boolean printModelMeta(final @NotNull ReqModelProjection<?, ?, ?> metaProjection) throws E {
    l.print("@");
    if (metaProjection.flag()) l.print("+");
    printModel(metaProjection, 0);
    return false;
  }

  @Override
  public void printModelOnly(@NotNull ReqModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqRecordModelProjection)
      print((ReqRecordModelProjection) mp, pathSteps);
    else if (mp instanceof ReqMapModelProjection)
      printModelOnly((ReqMapModelProjection) mp, pathSteps);
    else if (mp instanceof ReqListModelProjection)
      printModelOnly((ReqListModelProjection) mp, pathSteps);
  }

  @Override
  protected String fieldNamePrefix(final @NotNull ReqFieldProjection fieldProjection) {
    return fieldProjection.flag() ? "+" : "";
  }

  private void printModelOnly(ReqMapModelProjection mp, int pathSteps) throws E {
    @Nullable List<ReqKeyProjection> keys = mp.keys();

    if (pathSteps > 0) {
      l.beginIInd();
      l.print("/");
      brk();

//      if (mp.keysRequired()) l.print("+");

      if (keys != null && keys.size() == 1) {
        printReqKey(keys.iterator().next(), true);
      } else
        throw new IllegalArgumentException(
            String.format(
                "Encountered map projection with %s keys while still having %d path steps",
                keys == null ? "*" : Integer.valueOf(keys.size()),
                pathSteps
            )
        );

      brk();
      printEntity(mp.itemsProjection(), decSteps(pathSteps));
      l.end();
    } else {
      l.beginIInd();
      l.print("[");

      if (keys == null) {
        brk().print("*");
        brk();
      } else {
        boolean first = true;
        for (ReqKeyProjection key : keys) {
          if (first) {
            brk();
            first = false;
          } else {
            l.print(",");
            nbsp();
          }

          printReqKey(key, false);
        }
        if (!first) brk();
      }


      l.print("]");
      if (mp.keysRequired()) l.print("+");
      l.print("(");

      if (!isPrintoutEmpty(mp.itemsProjection())) {
        brk();
        printEntity(mp.itemsProjection(), 0);
      }
      brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  private void printModelOnly(ReqListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(");
    brk();
    printEntity(mp.itemsProjection(), 0);
    brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  @Override
  protected String modelTailTypeNamePrefix(final @NotNull ReqModelProjection<?, ?, ?> projection) {
    return projection.flag() ? "+" : super.modelTailTypeNamePrefix(projection);
  }

  @Override
  public boolean isPrintoutNoParamsEmpty(@NotNull ReqModelProjection<?, ?, ?> mp) {
    if (mp instanceof ReqMapModelProjection) {
      ReqMapModelProjection mapModelProjection = (ReqMapModelProjection) mp;
      /*@Nullable*/ List<ReqKeyProjection> keys = mapModelProjection.keys();
      return keys == null && isPrintoutEmpty(mapModelProjection.itemsProjection());
    } else
      return super.isPrintoutNoParamsEmpty(mp);
  }

}
