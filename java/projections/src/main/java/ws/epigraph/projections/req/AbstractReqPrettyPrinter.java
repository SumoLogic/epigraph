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
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractReqPrettyPrinter<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends AbstractReqModelProjection</*MP*/?, ?, ?>,
    E extends Exception> extends AbstractProjectionsPrettyPrinter<VP, TP, MP, E> {

  protected @NotNull DataPrinter<E> dataPrinter;
  protected @NotNull DataPrinter<E> paramsDataPrinter;

  protected AbstractReqPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<VP, MP> context) {
    super(layouter, context);

    dataPrinter = new DataPrinter<>(layouter, false);

    paramsDataPrinter = new DataPrinter<E>(layouter, true) {
      @Override
      protected Layouter<E> brk(final int i) { return lo; }

      @Override
      protected Layouter<E> brk(final int i, final int k) { return lo; }
    };
  }

  protected void printParams(@NotNull ReqParams params) throws E { // move to req common?
    l.beginCInd();
    if (!params.isEmpty()) {
      for (ReqParam param : params.asMap().values()) {
        l.beginIInd();
        l.print(";").print(param.name());
        brk().print("=");
        brk();
        paramsDataPrinter.print(null, param.value());
        l.end();
      }
    }
    l.end();
  }

  protected void printDirectives(@NotNull Directives directives) throws E {
    l.beginCInd();
    if (!directives.isEmpty()) {
      for (Directive directive : directives.asMap().values()) {
        l.beginIInd();
        l.print("!").print(directive.name());
        brk().print("=");
        brk();
        gdataPrettyPrinter.print(directive.value());
        l.end();
      }
    }
    l.end();
  }

  protected void printReqKey(final AbstractReqKeyProjection key, boolean bracketsAroundParams) throws E {
    dataPrinter.print(null, key.value());

    boolean keyParamsOrDirectivesNonEmpty = !key.params().isEmpty() || !key.directives().isEmpty();

    if (keyParamsOrDirectivesNonEmpty && bracketsAroundParams) {
      l.print("[");
    }

    printParams(key.params());
    printDirectives(key.directives());

    if (keyParamsOrDirectivesNonEmpty && bracketsAroundParams) {
      l.print("]");
    }
  }
}
