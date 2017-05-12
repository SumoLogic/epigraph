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

package ws.epigraph.wire.json.reader;

import com.fasterxml.jackson.core.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.projections.gen.GenProjectionsComparator;
import ws.epigraph.projections.req.output.*;
import ws.epigraph.wire.FormatReader;
import ws.epigraph.wire.ReqOutputFormatReader;
import ws.epigraph.wire.WireFormat;
import ws.epigraph.wire.json.JsonFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JSON data reader guided by req output projection
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputJsonFormatReader extends AbstractJsonFormatReader<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>,
    ReqOutputRecordModelProjection,
    ReqOutputFieldProjectionEntry,
    ReqOutputFieldProjection,
    ReqOutputMapModelProjection,
    ReqOutputListModelProjection
    > implements ReqOutputFormatReader {

  public ReqOutputJsonFormatReader(@NotNull JsonParser jsonParser) { super(jsonParser); }

  @Override
  protected GenProjectionsComparator<ReqOutputVarProjection, ReqOutputTagProjectionEntry, ReqOutputModelProjection<?, ?, ?>, ReqOutputRecordModelProjection, ReqOutputMapModelProjection, ReqOutputListModelProjection, ?, ReqOutputFieldProjectionEntry, ReqOutputFieldProjection> projectionsComparator() {
    return new ReqOutputProjectionsComparator(false, false);
  }

  @Override
  protected boolean tagRequired(@NotNull ReqOutputTagProjectionEntry tagProjection) {
    return tagProjection.projection().required();
  }

  @Override
  protected boolean fieldRequired(@NotNull ReqOutputFieldProjectionEntry fieldEntry) {
    return fieldEntry.fieldProjection().required();
  }

  @Override
  protected @Nullable Set<Datum> getExpectedKeys(
      @NotNull Collection<@NotNull ReqOutputMapModelProjection> projections
  ) {
    Set<Datum> expectedKeys = null;
    for (final ReqOutputMapModelProjection projection : projections) {
      final @Nullable List<ReqOutputKeyProjection> keyProjections = projection.keys();
      if (keyProjections == null) return null; //'*' : all keys allowed
      if (expectedKeys == null) expectedKeys = new HashSet<>();
      for (ReqOutputKeyProjection keyProjection : keyProjections) { expectedKeys.add(keyProjection.value()); }
    }
    return expectedKeys;
  }

  @Override
  protected @Nullable ReqOutputModelProjection<?, ?, ?> getMetaProjection(final @NotNull ReqOutputModelProjection<?, ?, ?> projection) {
    return projection.metaProjection();
  }

  public static class Factory implements FormatReader.Factory<ReqOutputJsonFormatReader> {
    @Override
    public @NotNull WireFormat format() { return JsonFormat.INSTANCE; }

    @Override
    public @NotNull ReqOutputJsonFormatReader newFormatReader(@NotNull InputStream is, @NotNull Charset charset) throws IOException {
      return new ReqOutputJsonFormatReader(AbstractJsonFormatReader.JSON_FACTORY.createParser(new InputStreamReader(is, charset)));
    }
  }
}
