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
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.req.AbstractReqProjectionsPrettyPrinter;
import ws.epigraph.projections.req.ReqParams;

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
    this(layouter, new ProjectionsPrettyPrinterContext<>(Qn.EMPTY));
  }

  @Override
  public void printTag(@Nullable String tagName, @NotNull ReqOutputTagProjectionEntry tp, int pathSteps) throws E {
    ReqOutputModelProjection<?, ?, ?> projection = tp.projection();

    ReqParams params = projection.params();
    Annotations annotations = projection.annotations();

    l.beginIInd(0);
    boolean needBrk = false;
    if (projection.required()) l.print("+");

    if (tagName != null) {
      l.print(tagName);
      needBrk = true;
    }

    if (!params.isEmpty()) {
      printParams(params);
      needBrk = true;
    }

    if (!annotations.isEmpty()) {
      printAnnotations(annotations);
      needBrk = true;
    }

    if (!isPrintoutEmpty(projection)) {
      if (needBrk) l.brk();
      printModel(projection, pathSteps);
    }

    ReqOutputModelProjection<?, ?, ?> metaProjection = projection.metaProjection();
    if (metaProjection != null) {
      l.print("@");
      if (metaProjection.required()) l.print("+");
      printModel(metaProjection, 0);
    }


    l.end();
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
      l.print("/").brk();

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

      l.brk();
      printVar(mp.itemsProjection(), decSteps(pathSteps));
      l.end();
    } else {
      l.beginIInd();
//      if (mp.keysRequired()) l.print("+");
      l.print("[");

      if (keys == null) {
        l.brk().print("*");
      } else {
        boolean first = true;
        for (ReqOutputKeyProjection key : keys) {
          if (first) {
            l.brk();
            first = false;
          } else l.print(", ");

          printReqKey(key);
        }
      }

      l.brk().print("](");

      if (!isPrintoutEmpty(mp.itemsProjection())) {
        l.brk();
        printVar(mp.itemsProjection(), 0);
      }
      l.brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  private void printModelOnly(ReqOutputListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(").brk();
    printVar(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  @Override
  protected String modelTailTypeNamePrefix(final @NotNull ReqOutputModelProjection<?, ?, ?> projection) {
    return projection.required() ? "+" : super.modelTailTypeNamePrefix(projection);
  }

  @Override
  public boolean isPrintoutEmpty(@NotNull ReqOutputModelProjection<?, ?, ?> mp) {
    if (mp instanceof ReqOutputMapModelProjection) {
      ReqOutputMapModelProjection mapModelProjection = (ReqOutputMapModelProjection) mp;
      @Nullable List<ReqOutputKeyProjection> keys = mapModelProjection.keys();
      return keys == null && isPrintoutEmpty(mapModelProjection.itemsProjection());
    } else
      return super.isPrintoutEmpty(mp);
  }

}
