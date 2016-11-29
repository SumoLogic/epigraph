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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.req.AbstractReqProjectionsPrettyPrinter;
import ws.epigraph.projections.req.ReqParams;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqDeleteProjectionsPrettyPrinter<E extends Exception>
    extends AbstractReqProjectionsPrettyPrinter<
    ReqDeleteVarProjection,
    ReqDeleteTagProjectionEntry,
    ReqDeleteModelProjection<?, ?>,
    ReqDeleteRecordModelProjection,
    ReqDeleteFieldProjectionEntry,
    ReqDeleteFieldProjection,
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
      print((ReqDeleteRecordModelProjection) mp, 0);
    else if (mp instanceof ReqDeleteMapModelProjection)
      print((ReqDeleteMapModelProjection) mp);
    else if (mp instanceof ReqDeleteListModelProjection)
      print((ReqDeleteListModelProjection) mp, pathSteps);
  }

  private void print(ReqDeleteMapModelProjection mp) throws E {
    printMapModelProjection(mp.keys(), mp.itemsProjection());
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

}
