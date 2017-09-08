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

package ws.epigraph.projections.req.output;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.AbstractReqProjectionsPrettyPrinter;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsPrettyPrinter<E extends Exception>
    extends AbstractReqProjectionsPrettyPrinter<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>,
    ReqOutputRecordModelProjection,
    ReqOutputFieldProjectionEntry,
    ReqOutputFieldProjection,
    E> {

  // todo: take var projection's 'parenthesized' into account

  public ReqOutputProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<ReqOutputVarProjection, ReqOutputModelProjection<?, ?, ?>> context) {
    super(layouter, context);
  }

  public ReqOutputProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.EMPTY, null));
  }

  @Override
  protected void printTagName(
      final @NotNull String tagName,
      final @NotNull ReqOutputModelProjection<?, ?, ?> projection) throws E {
    if (projection.required()) l.print("+");
    super.printTagName(tagName, projection);
  }

  @Override
  protected void printModelMeta(final @NotNull ReqOutputModelProjection<?, ?, ?> metaProjection) throws E {
    l.print("@");
    if (metaProjection.required()) l.print("+");
    printModel(metaProjection, 0);
  }

  @Override
  public void printModelOnly(@NotNull ReqOutputModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqOutputRecordModelProjection)
      print((ReqOutputRecordModelProjection) mp, pathSteps);
    else if (mp instanceof ReqOutputMapModelProjection)
      printModelOnly((ReqOutputMapModelProjection) mp, pathSteps);
    else if (mp instanceof ReqOutputListModelProjection)
      printModelOnly((ReqOutputListModelProjection) mp, pathSteps);
  }

  @Override
  protected String fieldNamePrefix(final @NotNull ReqOutputFieldProjection fieldProjection) {
    return fieldProjection.required() ? "+" : "";
  }

  private void printModelOnly(ReqOutputMapModelProjection mp, int pathSteps) throws E {
    @Nullable List<ReqOutputKeyProjection> keys = mp.keys();

    if (pathSteps > 0) {
      l.beginIInd();
      l.print("/");
      brk();

//      if (mp.keysRequired()) l.print("+");

      if (keys != null && keys.size() == 1) {
        printReqKey(keys.iterator().next());
      } else
        throw new IllegalArgumentException(
            String.format(
                "Encountered map projection with %s keys while still having %d path steps",
                keys == null ? "*" : Integer.valueOf(keys.size()),
                pathSteps
            )
        );

      brk();
      printVar(mp.itemsProjection(), decSteps(pathSteps));
      l.end();
    } else {
      l.beginIInd();
      l.print("[");

      if (keys == null) {
        brk().print("*");
      } else {
        boolean first = true;
        for (ReqOutputKeyProjection key : keys) {
          if (first) {
            brk();
            first = false;
          } else {
            l.print(",");
            nbsp();
          }

          printReqKey(key);
        }
      }

      brk().print("]");
      if (mp.keysRequired()) l.print("+");
      l.print("(");

      if (!isPrintoutEmpty(mp.itemsProjection())) {
        brk();
        printVar(mp.itemsProjection(), 0);
      }
      brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  private void printModelOnly(ReqOutputListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(");
    brk();
    printVar(mp.itemsProjection(), 0);
    brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  @Override
  protected String modelTailTypeNamePrefix(final @NotNull ReqOutputModelProjection<?, ?, ?> projection) {
    return projection.required() ? "+" : super.modelTailTypeNamePrefix(projection);
  }

  @Override
  public boolean isPrintoutNoParamsEmpty(@NotNull ReqOutputModelProjection<?, ?, ?> mp) {
    if (mp instanceof ReqOutputMapModelProjection) {
      ReqOutputMapModelProjection mapModelProjection = (ReqOutputMapModelProjection) mp;
      /*@Nullable*/ List<ReqOutputKeyProjection> keys = mapModelProjection.keys();
      return keys == null && isPrintoutEmpty(mapModelProjection.itemsProjection());
    } else
      return super.isPrintoutNoParamsEmpty(mp);
  }

}
