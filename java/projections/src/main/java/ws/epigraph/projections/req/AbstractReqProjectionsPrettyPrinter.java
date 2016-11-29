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

package ws.epigraph.projections.req;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractReqProjectionsPrettyPrinter<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?>,
    E extends Exception> extends AbstractProjectionsPrettyPrinter<VP, TP, MP, E> {

  protected AbstractReqProjectionsPrettyPrinter(final Layouter<E> layouter) {
    super(layouter);
  }

  protected void printParams(@NotNull ReqParams params) throws E { // move to req common?
    l.beginCInd();
    if (!params.isEmpty()) {
      for (ReqParam param : params.asMap().values()) {
        l.brk().beginIInd();
        l.print(";").print(param.name()).brk().print("=").brk();
        dataPrinter.print(param.value());
        l.end();
      }
    }
    l.end();
  }

  protected void printAnnotations(@NotNull Annotations annotations) throws E {
    l.beginCInd();
    if (!annotations.isEmpty()) {
      for (Annotation annotation : annotations.asMap().values()) {
        l.brk().beginIInd();
        l.print("!").print(annotation.name()).brk().print("=").brk();
        gdataPrettyPrinter.print(annotation.value());
        l.end();
      }
    }
    l.end();
  }

  protected void printReqKey(final ReqKeyProjection key) throws E {
    dataPrinter.print(key.value());
    printParams(key.params());
    printAnnotations(key.annotations());
  }

  protected void printMapModelProjection(@Nullable List<? extends ReqKeyProjection> keys, @NotNull VP itemsProjection)
      throws E {
    l.beginIInd();
    l.print("[");

    if (keys == null) {
      l.print("*");
    } else {
      boolean first = true;
      for (ReqKeyProjection key : keys) {
        if (first) {
          l.brk();
          first = false;
        }
        else l.print(", ");

        printReqKey(key);
      }
      if (!first) l.brk();
    }

    l.print("](");

    if (!isPrintoutEmpty(itemsProjection)) {
      l.brk();
      print(itemsProjection, 0);
    }
    l.brk(1, -l.getDefaultIndentation()).end().print(")");

  }
}
