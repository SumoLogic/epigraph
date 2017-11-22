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
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqProjectionsPrettyPrinter<E extends Exception> extends AbstractReqPrettyPrinter<E> {

  public ReqProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<ReqEntityProjection, ReqModelProjection<?, ?, ?>> context) {
    super(layouter, context);
  }

  public ReqProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.EMPTY, null));
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

  public void print(@NotNull ReqRecordModelProjection recordProjection, int pathSteps) throws E {
    Map<String, ReqFieldProjectionEntry> fieldProjections = recordProjection.fieldProjections();

    if (pathSteps > 0) {
      if (fieldProjections.isEmpty()) return;
      if (fieldProjections.size() > 1) throw new IllegalArgumentException(
          String.format("Encountered %d fields while still having %d path steps", fieldProjections.size(), pathSteps)
      );

      Map.Entry<String, ReqFieldProjectionEntry> entry = fieldProjections.entrySet().iterator().next();
      l.beginIInd();
      l.print("/");
      brk();
      print(entry.getKey(), entry.getValue().fieldProjection(), decSteps(pathSteps));
      l.end();

    } else {

      l.print("(").beginCInd();
      boolean first = true;
      for (Map.Entry<String, ReqFieldProjectionEntry> entry : fieldProjections.entrySet()) {
        if (first) first = false;
        else l.print(",");
        brk();

        print(entry.getKey(), entry.getValue().fieldProjection(), 0);

      }
      brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  public void print(@NotNull String fieldName, @NotNull ReqFieldProjection fieldProjection, int pathSteps) throws E {
    @NotNull ReqEntityProjection fieldEntityProjection = fieldProjection.projection();
//    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    l.beginIInd();
    l.print(fieldNamePrefix(fieldProjection));
    l.print(fieldName);

//    printParams(fieldProjection.params());
//    printAnnotations(fieldAnnotations);

    if (!isPrintoutEmpty(fieldEntityProjection)) {
      brk();
      printEntity(fieldEntityProjection, pathSteps - 1);
    }
    l.end();
  }

  public boolean isPrintoutEmpty(@NotNull ReqFieldProjection fieldProjection) {
    @NotNull ReqEntityProjection fieldEntityProjection = fieldProjection.projection();
//    @NotNull ReqParams fieldParams = fieldProjection.params();
//    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    return /*fieldParams.isEmpty() && fieldAnnotations.isEmpty() && */isPrintoutEmpty(fieldEntityProjection);
  }

  @Override
  protected boolean isPrintoutEmpty(@NotNull ReqEntityProjection ep) {

    if (!super.isPrintoutEmpty(ep)) return false;

    for (ReqTagProjectionEntry tagProjection : ep.tagProjections().values()) {
      final ReqModelProjection<?, ?, ?> modelProjection = tagProjection.projection();
      if (!modelProjection.params().isEmpty()) return false;
      if (!modelProjection.directives().isEmpty()) return false;
    }

    return true;
  }

  @Override
  public boolean modelParamsEmpty(final @NotNull ReqModelProjection<?, ?, ?> mp) {
    return super.modelParamsEmpty(mp) && mp.directives().isEmpty() && mp.params().isEmpty();
  }


//  protected void printMapModelProjection(@Nullable List<? extends AbstractReqKeyProjection> keys, @NotNull VP itemsProjection)
//      throws E {
//    printMapModelProjection(keys, "", itemsProjection);
//  }
//
//  protected void printMapModelProjection(
//      @Nullable List<? extends AbstractReqKeyProjection> keys,
//      @NotNull String itemsProjectionPrefix,
//      @NotNull VP itemsProjection)
//      throws E {
//
//    l.beginIInd();
//    l.print("[");
//
//    if (keys == null) {
//      l.print("*");
//    } else {
//      boolean first = true;
//      for (AbstractReqKeyProjection key : keys) {
//        if (first) {
//          brk();
//          first = false;
//        } else {
//          l.print(",");
//          nbsp(); // why not brk() ?
//        }
//
//        printReqKey(key);
//      }
//      if (!first) brk();
//    }
//
//    l.print("]");
//    l.print(itemsProjectionPrefix);
//    l.print("(");
//
//    if (!isPrintoutEmpty(itemsProjection)) {
//      brk();
//      printEntity(itemsProjection, 0);
//    }
//    brk(1, -l.getDefaultIndentation()).end().print(")");
////    brk().end();
//
//  }

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
      /*@Nullable*/
      List<ReqKeyProjection> keys = mapModelProjection.keys();
      return keys == null && isPrintoutEmpty(mapModelProjection.itemsProjection());
    } else
      return super.isPrintoutNoParamsEmpty(mp);
  }

}
