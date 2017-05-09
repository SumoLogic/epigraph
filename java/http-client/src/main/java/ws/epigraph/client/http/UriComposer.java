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

package ws.epigraph.client.http;

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputProjectionsPrettyPrinter;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UriComposer {

  public static @NotNull String composeReadUri(
      @NotNull String fieldName,
      @NotNull ReqOutputFieldProjection projection) {

    StringBackend sb = new StringBackend(2000);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);

    ReqOutputProjectionsPrettyPrinter<NoExceptions> prettyPrinter =
        new ReqOutputProjectionsPrettyPrinter<NoExceptions>(layouter) {
          @Override
          protected @NotNull Layouter<NoExceptions> brk() { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> brk(final int width, final int offset) { return layouter; }

          @Override
          protected @NotNull Layouter<NoExceptions> nbsp() { return layouter; }
        };

    prettyPrinter.print(fieldName, projection, 0);

    return sb.getString();
  }
}
