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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.MapDatum;
import ws.epigraph.data.RecordDatum;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Field;
import ws.epigraph.types.Tag;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractFormatWriter<
    P extends GenProjection<?, TP, EP, MP>,
    EP extends GenEntityProjection<EP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection<EP, TP, /*MP*/?, ?, ?>,
    RMP extends GenRecordModelProjection<P, TP, EP, MP, RMP, FPE, FP, ?>,
    FPE extends GenFieldProjectionEntry<P, TP, MP, FP>,
    FP extends GenFieldProjection<P, TP, MP, FP>,
    MMP extends GenMapModelProjection<P, TP, EP, MP, MMP, ?>>
    implements FormatWriter<P, EP, MP> {

  @Override
  public void writeData(@NotNull EP projection, int pathSteps, @Nullable Data data) throws IOException {
    if (pathSteps == 0) {
      writeData(projection, data);
    } else {

      if (data == null) {
        writeNull();
      } else {
        TP singleTagProjection = projection.singleTagProjection();
        assert singleTagProjection != null;
        Tag tag = (Tag) singleTagProjection.tag();

        ErrorValue error = data._raw().getError(tag);
        if (error == null) {
          writeDatum(
              singleTagProjection.modelProjection(),
              pathSteps - 1,
              data._raw().getDatum(tag)
          );
        } else {
          writeError(error);
        }
      }

    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void writeDatum(@NotNull MP projection, int pathSteps, @Nullable Datum datum) throws IOException {
    if (pathSteps == 0) {
      writeDatum(projection, datum);
    } else {

      if (datum == null) {
        writeNull();
      } else {

        DatumType type = (DatumType) projection.type();
        switch (type.kind()) {

          case RECORD:
            RecordDatum recordDatum = (RecordDatum) datum;
            RMP recordProjection = (RMP) projection;
            Set<Map.Entry<String, FPE>> fieldEntries = recordProjection.fieldProjections().entrySet();
            assert fieldEntries.size() == 1;
            FPE fpe = fieldEntries.iterator().next().getValue();

            write(
                fpe.fieldProjection().projection(),
                pathSteps - 1,
                recordDatum._raw().getData((Field) fpe.field())
            );
            break;

          case MAP:
            MapDatum mapDatum = (MapDatum) datum;
            MMP mapProjection = (MMP) projection;

            Collection<Datum> expectedKeys = getExpectedKeys(mapProjection);
            assert expectedKeys != null && expectedKeys.size() == 1;

            write(
                mapProjection.itemsProjection(),
                pathSteps - 1,
                mapDatum._raw().elements().get(
                    expectedKeys.iterator().next().toImmutable()
                )
            );
            break;

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
  }

  protected abstract void writeData(@NotNull EP projection, @Nullable Data data) throws IOException;

  protected abstract void writeDatum(@NotNull MP projection, @Nullable Datum datum) throws IOException;

  protected abstract @Nullable Collection<Datum> getExpectedKeys(@NotNull MMP mapProjection);

  protected abstract void writeNull() throws IOException;
}
