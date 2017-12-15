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
import ws.epigraph.projections.req.*;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.wire.FormatReader;
import ws.epigraph.wire.ReqFormatReader;
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
public class ReqJsonFormatReader extends AbstractJsonFormatReader<
    ReqProjection<?, ?>,
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>,
    ReqRecordModelProjection,
    ReqFieldProjectionEntry,
    ReqFieldProjection,
    ReqMapModelProjection,
    ReqListModelProjection
    > implements ReqFormatReader {

  public ReqJsonFormatReader(@NotNull JsonParser jsonParser, @NotNull TypesResolver typesResolver) {
    super(jsonParser, typesResolver);
  }

  @Override
  protected GenProjectionsComparator<ReqProjection<?, ?>, ReqTagProjectionEntry, ReqEntityProjection, ReqModelProjection<?, ?, ?>, ReqRecordModelProjection, ReqMapModelProjection, ReqListModelProjection, ?, ReqFieldProjectionEntry, ReqFieldProjection> projectionsComparator() {
    return new ReqProjectionsComparator(false, false);
  }

  @Override
  protected boolean tagRequired(@NotNull ReqTagProjectionEntry tagProjection) {
    return tagProjection.modelProjection().flag();
  }

  @Override
  protected boolean fieldRequired(@NotNull ReqFieldProjectionEntry fieldEntry) {
    return fieldEntry.fieldProjection().flag();
  }

  @Override
  protected @Nullable Set<Datum> getExpectedKeys(@NotNull Collection<@NotNull ReqMapModelProjection> projections) {
    Set<Datum> expectedKeys = null;
    for (final ReqMapModelProjection projection : projections) {
      final @Nullable List<ReqKeyProjection> keyProjections = projection.keys();
      if (keyProjections == null) return null; //'*' : all keys allowed
      if (expectedKeys == null) expectedKeys = new HashSet<>();
      for (ReqKeyProjection keyProjection : keyProjections) { expectedKeys.add(keyProjection.value()); }
    }
    return expectedKeys;
  }

  @Override
  protected @Nullable ReqModelProjection<?, ?, ?> getMetaProjection(final @NotNull ReqModelProjection<?, ?, ?> projection) {
    return projection.metaProjection();
  }

  public static class Factory implements FormatReader.Factory<ReqJsonFormatReader> {
    @Override
    public @NotNull WireFormat format() { return JsonFormat.INSTANCE; }

    @Override
    public @NotNull ReqJsonFormatReader newFormatReader(
        @NotNull InputStream is,
        @NotNull Charset charset,
        @NotNull TypesResolver typesResolver) throws IOException {

      return new ReqJsonFormatReader(
          AbstractJsonFormatReader.JSON_FACTORY.createParser(new InputStreamReader(is, charset)), typesResolver
      );
    }
  }
}
