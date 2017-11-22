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

package ws.epigraph.server.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.MapDatum;
import ws.epigraph.data.RecordDatum;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.req.*;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Field;
import ws.epigraph.types.Tag;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DataErrorLocator {
  private DataErrorLocator() {}

  /**
   * Checks if data at the end of the path contains an error
   *
   * @param projection data projection
   * @param pathSteps  number of steps in the path
   * @param data       data
   *
   * @return error value if data contains an error after {@code pathSteps} along the path, {@code null} otherwise
   */
  public static @Nullable ErrorValue getError(
      @NotNull ReqEntityProjection projection,
      int pathSteps,
      @Nullable Data data) {

    if (data == null || pathSteps == 0)
      return null;

    ReqTagProjectionEntry singleTagProjection = projection.singleTagProjection();
    assert singleTagProjection != null;
    Tag tag = (Tag) singleTagProjection.tag();

    ErrorValue error = data._raw().getError(tag);
    if (error == null) {
      return getError(
          singleTagProjection.projection(),
          pathSteps - 1,
          data._raw().getDatum(tag)
      );
    } else {
      return error;
    }
  }

  private static @Nullable ErrorValue getError(
      @NotNull ReqModelProjection<?, ?, ?> projection,
      int pathSteps,
      @Nullable Datum datum) {

    if (datum == null || pathSteps == 0)
      return null;

    DatumType type = (DatumType) projection.type();
    switch (type.kind()) {

      case RECORD:
        RecordDatum recordDatum = (RecordDatum) datum;
        ReqRecordModelProjection recordProjection = (ReqRecordModelProjection) projection;
        Set<Map.Entry<String, ReqFieldProjectionEntry>> fieldEntries = recordProjection.fieldProjections().entrySet();
        assert fieldEntries.size() == 1;
        ReqFieldProjectionEntry fpe = fieldEntries.iterator().next().getValue();

        return getError(
            fpe.fieldProjection().projection(),
            pathSteps - 1,
            recordDatum._raw().getData((Field) fpe.field())
        );

      case MAP:
        MapDatum mapDatum = (MapDatum) datum;
        ReqMapModelProjection mapProjection = (ReqMapModelProjection) projection;

        @Nullable List<ReqKeyProjection> expectedKeys = mapProjection.keys();
        assert expectedKeys != null && expectedKeys.size() == 1;

        return getError(
            mapProjection.itemsProjection(),
            pathSteps - 1,
            mapDatum._raw().elements().get(
                expectedKeys.iterator().next().value().toImmutable()
            )
        );

      default:
        throw new IllegalStateException(
            String.format(
                "Unexpected model kind '%s' while %d path steps still left",
                type.kind(),
                pathSteps
            )
        );

    }
  }
}
