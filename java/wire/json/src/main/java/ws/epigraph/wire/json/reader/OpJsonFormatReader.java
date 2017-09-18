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
import ws.epigraph.projections.op.output.*;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.wire.FormatReader;
import ws.epigraph.wire.OpFormatReader;
import ws.epigraph.wire.WireFormat;
import ws.epigraph.wire.json.JsonFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JSON data reader guided by req output projection
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpJsonFormatReader extends AbstractJsonFormatReader<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?, ?>,
    OpOutputRecordModelProjection,
    OpOutputFieldProjectionEntry,
    OpOutputFieldProjection,
    OpOutputMapModelProjection,
    OpOutputListModelProjection
    > implements OpFormatReader {

  @Override
  protected GenProjectionsComparator<OpOutputVarProjection, OpOutputTagProjectionEntry, OpOutputModelProjection<?, ?, ?, ?>, OpOutputRecordModelProjection, OpOutputMapModelProjection, OpOutputListModelProjection, ?, OpOutputFieldProjectionEntry, OpOutputFieldProjection> projectionsComparator() {
    return new GenProjectionsComparator<>();
  }

  public OpJsonFormatReader(@NotNull JsonParser jsonParser, @NotNull TypesResolver typeResolver) {
    super(
        jsonParser,
        typeResolver
    );
  }

  @Override
  protected boolean tagRequired(final @NotNull OpOutputTagProjectionEntry tagProjection) {
    return tagProjection.projection().flagged();
  }

  @Override
  protected boolean fieldRequired(final @NotNull OpOutputFieldProjectionEntry fieldEntry) {
    return fieldEntry.fieldProjection().flagged();
  }

  @Override
  protected @Nullable List<? extends OpOutputModelProjection<?, ?, ?, ?>>
  getKeyProjections(@NotNull Collection<OpOutputMapModelProjection> projections) {
    List<? extends OpOutputModelProjection<?, ?, ?, ?>> keyProjections = projections.stream()
        .map(mp -> mp.keyProjection().projection())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    return keyProjections.isEmpty() ? null : keyProjections;
  }

  @Override
  protected @Nullable Set<Datum> getExpectedKeys(final @NotNull Collection<OpOutputMapModelProjection> projections) {
    return null;
  }

  @Override
  protected @Nullable OpOutputModelProjection<?, ?, ?, ?> getMetaProjection(final @NotNull OpOutputModelProjection<?, ?, ?, ?> projection) {
    return projection.metaProjection();
  }

  public static class Factory implements FormatReader.Factory<OpJsonFormatReader> {
    @Override
    public @NotNull WireFormat format() { return JsonFormat.INSTANCE; }

    @Override
    public @NotNull OpJsonFormatReader newFormatReader(
        @NotNull InputStream is,
        @NotNull Charset charset,
        @NotNull TypesResolver typesResolver)
        throws IOException {

      return new OpJsonFormatReader(
          AbstractJsonFormatReader.JSON_FACTORY.createParser(new InputStreamReader(is, charset)), typesResolver
      );
    }
  }
}
