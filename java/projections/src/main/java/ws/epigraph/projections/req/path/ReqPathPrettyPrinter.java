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

package ws.epigraph.projections.req.path;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.projections.req.ReqParam;
import ws.epigraph.projections.req.ReqParams;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<ReqVarPath, ReqTagPath, ReqModelPath<?, ?, ?>, E> {

  protected @NotNull DataPrinter<E> dataPrinter;

  public ReqPathPrettyPrinter(Layouter<E> layouter) {
    super(layouter);
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
  public void print(@Nullable String tagName, @NotNull ReqTagPath tp, int pathSteps) throws E {
    ReqModelPath<?, ?, ?> projection = tp.projection();

    ReqParams params = projection.params();
    Annotations annotations = projection.annotations();

    l.beginCInd();
    boolean needBrk = false;

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
      print(projection, pathSteps);
    }

    l.end();
  }

  @Override
  public void print(@NotNull ReqModelPath<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqRecordModelPath)
      print((ReqRecordModelPath) mp);
    else if (mp instanceof ReqMapModelPath)
      print((ReqMapModelPath) mp);
  }

  private void print(@NotNull ReqRecordModelPath mp) throws E {
    final @Nullable ReqFieldPathEntry fieldProjectionEntry = mp.pathFieldProjection();

    if (fieldProjectionEntry != null) {
      l.beginIInd();
      l.print("/").brk();
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
      l.brk();
      print(fieldVarProjection, 1);
    }
    l.end();
  }

  private void print(ReqMapModelPath mp) throws E {
    final @NotNull ReqPathKeyProjection key = mp.key();

    l.beginIInd();
    l.print("/").brk();

    dataPrinter.print(key.value());
    printParams(key.params());
    printAnnotations(key.annotations());

    if (!isPrintoutEmpty(mp.itemsProjection())) l.brk();

    print(mp.itemsProjection(), 1);
    l.end();
  }

  @Override
  public boolean isPrintoutEmpty(@NotNull ReqModelPath<?, ?, ?> mp) {
    if (mp instanceof ReqRecordModelPath) {
      ReqRecordModelPath recordModelProjection = (ReqRecordModelPath) mp;
      Map<String, ReqFieldPathEntry> fieldProjections = recordModelProjection.fieldProjections();
      return fieldProjections.isEmpty();
    }

    return !(mp instanceof ReqMapModelPath); // map key always present
  }

  private void printParams(@NotNull ReqParams params) throws E { // move to req common?
    if (!params.isEmpty()) {
      for (ReqParam param : params.asMap().values()) {
        l.brk().beginIInd();
        l.print(";").print(param.name()).brk().print("=").brk();
        dataPrinter.print(param.value());
        l.end();
      }
    }
  }

  @Override
  protected boolean isPrintoutEmpty(@NotNull ReqVarPath varPath) {
    // no tags = end of path = empty printout
    return varPath.tagProjections().isEmpty() || super.isPrintoutEmpty(varPath);
  }

  private void printAnnotations(@NotNull Annotations annotations) throws E {
    if (!annotations.isEmpty()) {
      for (Annotation annotation : annotations.asMap().values()) {
        l.brk().beginIInd();
        l.print("!").print(annotation.name()).brk().print("=").brk();
        gdataPrettyPrinter.print(annotation.value());
        l.end();
      }
    }
  }
}
