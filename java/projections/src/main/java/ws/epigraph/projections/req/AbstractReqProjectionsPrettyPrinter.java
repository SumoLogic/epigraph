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
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.GenFieldProjectionEntry;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenEntityProjection;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractReqProjectionsPrettyPrinter<
    VP extends GenEntityProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends AbstractReqModelProjection</*MP*/?, ?, ?>,
    RP extends GenRecordModelProjection<VP, TP, MP, RP, FPE, FP, ?>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends AbstractReqFieldProjection<VP, TP, MP, FP>,
    E extends Exception> extends AbstractReqPrettyPrinter<VP, TP, MP, E> {

  protected AbstractReqProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<VP, MP> context) {
    super(layouter, context);

  }

  @Override
  protected boolean printModelParams(final @NotNull MP projection) throws E {
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
  protected boolean printModelMeta(final @NotNull MP metaProjection) throws E {
    l.print("@");
    printModel(metaProjection, 0);
    return false;
  }

  public void print(@NotNull RP recordProjection, int pathSteps) throws E {
    Map<String, FPE> fieldProjections = recordProjection.fieldProjections();

    if (pathSteps > 0) {
      if (fieldProjections.isEmpty()) return;
      if (fieldProjections.size() > 1) throw new IllegalArgumentException(
          String.format("Encountered %d fields while still having %d path steps", fieldProjections.size(), pathSteps)
      );

      Map.Entry<String, FPE> entry = fieldProjections.entrySet().iterator().next();
      l.beginIInd();
      l.print("/");
      brk();
      print(entry.getKey(), entry.getValue().fieldProjection(), decSteps(pathSteps));
      l.end();

    } else {

      l.print("(").beginCInd();
      boolean first = true;
      for (Map.Entry<String, FPE> entry : fieldProjections.entrySet()) {
        if (first) first = false;
        else l.print(",");
        brk();

        print(entry.getKey(), entry.getValue().fieldProjection(), 0);

      }
      brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  protected String fieldNamePrefix(@NotNull FP fieldProjection) { return ""; }

  public void print(@NotNull String fieldName, @NotNull FP fieldProjection, int pathSteps) throws E {
    @NotNull VP fieldVarProjection = fieldProjection.entityProjection();
//    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    l.beginIInd();
    l.print(fieldNamePrefix(fieldProjection));
    l.print(fieldName);

//    printParams(fieldProjection.params());
//    printAnnotations(fieldAnnotations);

    if (!isPrintoutEmpty(fieldVarProjection)) {
      brk();
      printEntity(fieldVarProjection, pathSteps - 1);
    }
    l.end();
  }

  public boolean isPrintoutEmpty(@NotNull FP fieldProjection) {
    @NotNull VP fieldVarProjection = fieldProjection.entityProjection();
//    @NotNull ReqParams fieldParams = fieldProjection.params();
//    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    return /*fieldParams.isEmpty() && fieldAnnotations.isEmpty() && */isPrintoutEmpty(fieldVarProjection);
  }

  @Override
  protected boolean isPrintoutEmpty(@NotNull VP vp) {

    if (!super.isPrintoutEmpty(vp)) return false;

    for (TP tagProjection : vp.tagProjections().values()) {
      final MP modelProjection = tagProjection.projection();
      if (!modelProjection.params().isEmpty()) return false;
      if (!modelProjection.directives().isEmpty()) return false;
    }

    return true;
  }

  @Override
  public boolean modelParamsEmpty(final @NotNull MP mp) {
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
}
