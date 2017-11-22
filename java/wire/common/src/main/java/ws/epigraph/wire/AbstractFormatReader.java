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

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.*;
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractFormatReader
    <
        EP extends GenEntityProjection<EP, TP, MP>,
        TP extends GenTagProjectionEntry<TP, MP>,
        MP extends GenModelProjection<TP, /*MP*/?, ?, ?, ?>,
        RMP extends GenRecordModelProjection<EP, TP, MP, RMP, FPE, FP, ?>,
        FPE extends GenFieldProjectionEntry<EP, TP, MP, FP>,
        FP extends GenFieldProjection<EP, TP, MP, FP>,
        MMP extends GenMapModelProjection<EP, TP, MP, MMP, ?>> implements FormatReader<EP, MP> {

  @Override
  public @Nullable Data readData(@NotNull EP projection, final int pathSteps) throws IOException, FormatException {
    if (pathSteps == 0) {
      return readData(projection);
    } else {
      Data.Builder data = ((Type) projection.type()).createDataBuilder();
      TP singleTagProjection = projection.singleTagProjection();

      if (singleTagProjection == null) {
        throw error(String.format(
            "Invalid projection: %d tags found while 1 was expected (%d left)",
            projection.tagProjections().size(),
            pathSteps
        ));
      }

      data._raw().setValue(
          (Tag) singleTagProjection.tag(),
          readValue(
              singleTagProjection.projection(),
              pathSteps - 1
          )
      );

      return data;
    }
  }

  protected abstract @Nullable Data readData(@NotNull EP projection) throws IOException, FormatException;

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull Val readValue(@NotNull MP projection, final int pathSteps) throws IOException, FormatException {
    if (pathSteps == 0) {
      return readValue(projection);
    } else {
      DatumType type = (DatumType) projection.type();
      switch (type.kind()) {
        case RECORD:
          RecordType recordType = (RecordType) type;
          RMP recordProjection = (RMP) projection;

          Set<Map.Entry<String, FPE>> fieldEntries = recordProjection.fieldProjections().entrySet();
          if (fieldEntries.size() != 1) {
            throw error(
                String.format(
                    "Record path should contain exactly one field, %d found (%d path steps left)",
                    fieldEntries.size(),
                    pathSteps
                )
            );
          }

          FPE fieldEntry = fieldEntries.iterator().next().getValue();

          RecordDatum.Builder recordDatum = recordType.createBuilder();
          recordDatum._raw().setData(
              (Field) fieldEntry.field(),
              readData(fieldEntry.fieldProjection().projection(), pathSteps - 1)
          );

          return recordDatum.asValue();
        case MAP:
          MapType mapType = (MapType) type;
          MMP mapProjection = (MMP) projection;

          Set<Datum> keys = getExpectedKeys(Collections.singleton(mapProjection));
          if (keys == null || keys.size() != 1) {
            throw error(
                String.format(
                    "Map path should contain exactly one key, %s found (%d path steps left)",
                    keys == null ? "*" : Integer.toString(keys.size()),
                    pathSteps
                )
            );
          }

          MapDatum.Builder mapDatum = mapType.createBuilder();
          Data data = readData(mapProjection.itemsProjection(), pathSteps - 1);

          if (data == null) {
            StringBuilder sb = new StringBuilder();
            Layouter<NoExceptions> layouter = Layouter.getStringLayouter(sb);
            new DataPrinter<>(layouter).print(mapType.keyType(), keys.iterator().next());

            throw error(String.format("Null map values are not allowed (for key '%s')", sb.toString()));
          }

          mapDatum._raw().elements().put(
              keys.iterator().next().toImmutable(),
              data
          );

          return mapDatum.asValue();
        default:
          throw error(
              String.format(
                  "Type '%s' (of %s kind) can't be part of the path (%d path steps left)",
                  type.name(),
                  type.kind(),
                  pathSteps
              )
          );
      }
    }
  }

  protected abstract @NotNull Val readValue(@NotNull MP projection) throws IOException, FormatException;

  /**
   * Get all keys from all projections or {@code null} if at least one projection tells
   * to include all keys (uses asterisk)
   */
  protected abstract @Nullable Set<Datum> getExpectedKeys(@NotNull Collection<MMP> projections);

  protected abstract FormatException error(@NotNull String message);
}
