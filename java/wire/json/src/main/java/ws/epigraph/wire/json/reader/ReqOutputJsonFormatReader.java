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

package ws.epigraph.wire.json.reader;

import com.fasterxml.jackson.core.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.RecordDatum;
import ws.epigraph.projections.req.output.*;
import ws.epigraph.types.RecordType;
import ws.epigraph.types.Type;
import ws.epigraph.wire.json.JsonFormatCommon;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JSON data reader guided by req output projection
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputJsonFormatReader extends AbstractJsonFormatReader<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?>,
    ReqOutputRecordModelProjection,
    ReqOutputFieldProjectionEntry,
    ReqOutputFieldProjection,
    ReqOutputMapModelProjection,
    ReqOutputListModelProjection
    > {


  public ReqOutputJsonFormatReader(@NotNull JsonParser jsonParser) { super(jsonParser); }

  @NotNull
  @Override
  protected Data finishReadingMultiData(
      @NotNull final Type effectiveType,
      @NotNull final List<? extends ReqOutputVarProjection> projections) throws IOException {

    Data data = super.finishReadingMultiData(effectiveType, projections);

    for (final ReqOutputVarProjection projection : projections) {
      for (final ReqOutputTagProjectionEntry tagProjectionEntry : projection.tagProjections().values()) {
        if (tagProjectionEntry.projection().required() && data._raw().getValue(tagProjectionEntry.tag()) == null)
          throw error("Missing data for required tag '" + tagProjectionEntry.tag().name() + "'");
      }
    }

    return data;
  }

  @NotNull
  @Override
  protected RecordDatum finishReadingRecord(
      final String fieldName,
      @NotNull final RecordType type,
      @NotNull final Collection<? extends ReqOutputRecordModelProjection> projections)
      throws IOException {

    RecordDatum datum = super.finishReadingRecord(fieldName, type, projections);

    for (ReqOutputRecordModelProjection projection : projections)
      for (final Map.Entry<String, ReqOutputFieldProjectionEntry> entry : projection.fieldProjections().entrySet())
        if (entry.getValue().fieldProjection().required() && datum._raw().getData(entry.getValue().field()) == null)
          throw error("Required field '" + entry.getKey() + "' is missing");

    return datum;
  }

  @Nullable
  @Override
  protected String monoTag(@NotNull final Iterable<? extends ReqOutputVarProjection> projections) {
    return JsonFormatCommon.monoTag(projections);
  }

  @Nullable
  @Override
  protected Set<Datum> getExpectedKeys(@NotNull final Collection<? extends ReqOutputMapModelProjection> projections) {
    Set<Datum> expectedKeys = null;

    for (final ReqOutputMapModelProjection projection : projections) {
      @Nullable final List<ReqOutputKeyProjection> keyProjections = projection.keys();
      if (keyProjections == null) return null; // '*' : all keys allowed

      if (expectedKeys == null) expectedKeys = new HashSet<>();
      expectedKeys.addAll(keyProjections.stream().map(ReqOutputKeyProjection::value).collect(Collectors.toList()));
    }

    return expectedKeys;
  }

}
