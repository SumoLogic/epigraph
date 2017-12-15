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

package ws.epigraph.wire;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.projections.gen.GenProjection;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;
import ws.epigraph.types.TypeApi;

import java.util.Collection;
import java.util.Deque;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class WireUtil {
  public static <P extends GenProjection<? extends P, ?, ?, ?>> boolean needPoly(@NotNull Deque<? extends P> ps) {
    if (ps.isEmpty()) return false;
    return needPoly(
        ps.peekLast().type(),
        ps
    );
  }

  public static <P extends GenProjection<? extends P, ?, ?, ?>> boolean needPoly(
      @NotNull TypeApi typeBound,
      @NotNull Collection<? extends P> ps) {

    for (final P p : ps) {
      List<? extends P> tails = p.polymorphicTails();
      if (tails != null) {
        for (final P tail : tails) {
          if (typeBound.isAssignableFrom(tail.type()) && !typeBound.equals(tail.type()))
            return true;
        }
      }
    }

    return false;
  }

  public static @NotNull TypeApi type(@NotNull Data data) {
    // helper method designed to resolve discrepancy between data and datum types for self-entity data
    // e.g. a UserRecord datum can be wrapped in PersonRecord data type, e.g. when data
    // is constructed using generated builders

    // see also: AbstractJsonFormatReader::finishReadingData
    Type type = data.type();
    if (type instanceof DatumType) {
      DatumType datumType = (DatumType) type;
      Datum datum = data._raw().getDatum(datumType.self());
      if (datum != null)
        return datum.type();
    }

    return type;
  }

}
