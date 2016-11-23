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

package ws.epigraph.projections.req.input;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.req.AbstractReqProjectionsPrettyPrinter;
import ws.epigraph.projections.req.ReqParams;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqInputProjectionsPrettyPrinter<E extends Exception>
    extends AbstractReqProjectionsPrettyPrinter<
    ReqInputVarProjection,
    ReqInputTagProjectionEntry,
    ReqInputModelProjection<?, ?>,
    E> {

  public ReqInputProjectionsPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
  }

  @Override
  public void print(@NotNull String tagName, @NotNull ReqInputTagProjectionEntry tp, int pathSteps) throws E {
    ReqInputModelProjection<?, ?> projection = tp.projection();

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
  public void print(@NotNull ReqInputModelProjection<?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqInputRecordModelProjection)
      print((ReqInputRecordModelProjection) mp);
    else if (mp instanceof ReqInputMapModelProjection)
      print((ReqInputMapModelProjection) mp);
    else if (mp instanceof ReqInputListModelProjection)
      print((ReqInputListModelProjection) mp, pathSteps);
  }

  private void print(@NotNull ReqInputRecordModelProjection mp) throws E {
    Map<String, ReqInputFieldProjectionEntry> fieldProjections = mp.fieldProjections();

    l.print("(").beginCInd();
    boolean first = true;
    for (Map.Entry<String, ReqInputFieldProjectionEntry> entry : fieldProjections.entrySet()) {
      if (first) first = false;
      else l.print(",");
      l.brk();

      print(entry.getKey(), entry.getValue().fieldProjection(), 0);

    }
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  public void print(@NotNull String fieldName, @NotNull ReqInputFieldProjection fieldProjection, int pathSteps)
      throws E {

    @NotNull ReqInputVarProjection fieldVarProjection = fieldProjection.varProjection();
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

  private void print(ReqInputMapModelProjection mp) throws E {
    printMapModelProjection(mp.keys(), mp.itemsProjection());
  }

  private void print(ReqInputListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(").brk();
    print(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }


  @Override
  public boolean isPrintoutEmpty(@NotNull ReqInputModelProjection<?, ?> mp) {
    if (mp instanceof ReqInputRecordModelProjection) {
      ReqInputRecordModelProjection recordModelProjection = (ReqInputRecordModelProjection) mp;
      Map<String, ReqInputFieldProjectionEntry> fieldProjections =
          recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    if (mp instanceof ReqInputMapModelProjection) {
      ReqInputMapModelProjection mapModelProjection = (ReqInputMapModelProjection) mp;
      return isPrintoutEmpty(mapModelProjection.itemsProjection());
    }

    if (mp instanceof ReqInputListModelProjection) {
      ReqInputListModelProjection inputListModelProjection = (ReqInputListModelProjection) mp;
      return isPrintoutEmpty(inputListModelProjection.itemsProjection());
    }

    return true;
  }

}
