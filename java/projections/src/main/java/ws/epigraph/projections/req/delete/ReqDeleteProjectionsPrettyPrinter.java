/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.projections.req.delete;

import de.uka.ilkd.pp.Layouter;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.projections.req.ReqParam;
import ws.epigraph.projections.req.ReqParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteProjectionsPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<
    ReqDeleteVarProjection,
    ReqDeleteTagProjectionEntry,
    ReqDeleteModelProjection<?, ?>,
    E> {

  public ReqDeleteProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull String tagName, @NotNull ReqDeleteTagProjectionEntry tp, int pathSteps) throws E {
    ReqDeleteModelProjection<?, ?> projection = tp.projection();

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
  public void print(@NotNull ReqDeleteModelProjection<?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqDeleteRecordModelProjection)
      print((ReqDeleteRecordModelProjection) mp, pathSteps);
    else if (mp instanceof ReqDeleteMapModelProjection)
      print((ReqDeleteMapModelProjection) mp, pathSteps);
    else if (mp instanceof ReqDeleteListModelProjection)
      print((ReqDeleteListModelProjection) mp, pathSteps);
  }

  private void print(@NotNull ReqDeleteRecordModelProjection mp, int pathSteps) throws E {
    Map<String, ReqDeleteFieldProjectionEntry> fieldProjections = mp.fieldProjections();

    if (pathSteps > 0) {
      if (fieldProjections.isEmpty()) return;
      if (fieldProjections.size() > 1) throw new IllegalArgumentException(
          String.format("Encountered %d fields while still having %d path steps", fieldProjections.size(), pathSteps)
      );

      Map.Entry<String, ReqDeleteFieldProjectionEntry> entry = fieldProjections.entrySet().iterator().next();
      l.beginIInd();
      l.print("/").brk();
      print(entry.getKey(), entry.getValue().projection(), decSteps(pathSteps));
      l.end();

    } else {

      l.print("(").beginCInd();
      boolean first = true;
      for (Map.Entry<String, ReqDeleteFieldProjectionEntry> entry : fieldProjections.entrySet()) {
        if (first) first = false;
        else l.print(",");
        l.brk();

        print(entry.getKey(), entry.getValue().projection(), 0);

      }
      l.brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  public void print(@NotNull String fieldName, @NotNull ReqDeleteFieldProjection fieldProjection, int pathSteps)
      throws E {

    @NotNull ReqDeleteVarProjection fieldVarProjection = fieldProjection.projection();
    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

    l.beginIInd();
    l.print(fieldName);

    printParams(fieldProjection.reqParams());
    printAnnotations(fieldAnnotations);

    if (!isPrintoutEmpty(fieldVarProjection)) {
      l.brk();
      print(fieldVarProjection, pathSteps);
    }
    l.end();
  }

  private void print(ReqDeleteMapModelProjection mp, int pathSteps) throws E {
    @Nullable List<ReqDeleteKeyProjection> keys = mp.keys();

    if (pathSteps > 0) {
      l.beginIInd();
      l.print("/").brk();

//      if (mp.keysRequired()) l.print("+");

      if (keys != null && keys.size() == 1) {
        ReqDeleteKeyProjection key = keys.iterator().next();
        dataPrinter.print(key.value());
        printParams(key.params());
        printAnnotations(key.annotations());
      } else
        throw new IllegalArgumentException(
            String.format(
                "Encountered map projection with %s keys while still having %d path steps",
                keys == null ? "*" : Integer.valueOf(keys.size()),
                pathSteps
            )
        );

      l.brk();
      print(mp.itemsProjection(), decSteps(pathSteps));
      l.end();
    } else {
      l.beginIInd();
//      if (mp.keysRequired()) l.print("+");
      l.print("[").brk();

      if (keys == null) {
        l.print("*");
      } else {
        boolean first = true;
        for (ReqDeleteKeyProjection key : keys) {
          if (first) first = false;
          else l.print(", ");

          dataPrinter.print(key.value());
          printParams(key.params());
          printAnnotations(key.annotations());
        }
      }

      l.brk().print("](");

      if (!isPrintoutEmpty(mp.itemsProjection())) {
        l.brk();
        print(mp.itemsProjection(), 0);
      }
      l.brk(1, -l.getDefaultIndentation()).end().print(")");
    }
  }

  private void print(ReqDeleteListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(").brk();
    print(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }


  @Override
  public boolean isPrintoutEmpty(@NotNull ReqDeleteModelProjection<?, ?> mp) {
    if (mp instanceof ReqDeleteRecordModelProjection) {
      ReqDeleteRecordModelProjection recordModelProjection = (ReqDeleteRecordModelProjection) mp;
      Map<String, ReqDeleteFieldProjectionEntry> fieldProjections =
          recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    if (mp instanceof ReqDeleteMapModelProjection) {
      ReqDeleteMapModelProjection mapModelProjection = (ReqDeleteMapModelProjection) mp;
      @Nullable List<ReqDeleteKeyProjection> keys = mapModelProjection.keys();
      return keys == null && isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    if (mp instanceof ReqDeleteListModelProjection) {
      ReqDeleteListModelProjection inputListModelProjection = (ReqDeleteListModelProjection) mp;
      return isPrintoutEmpty(inputListModelProjection.itemsProjection());
    }

    return true;
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
