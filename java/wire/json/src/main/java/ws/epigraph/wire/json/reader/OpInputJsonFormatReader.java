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
import ws.epigraph.projections.op.input.*;
import ws.epigraph.wire.FormatReader;
import ws.epigraph.wire.OpInputFormatReader;
import ws.epigraph.wire.WireFormat;
import ws.epigraph.wire.json.JsonFormat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

/**
 * JSON data reader guided by req output projection
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputJsonFormatReader extends AbstractJsonFormatReader<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?, ?>,
    OpInputRecordModelProjection,
    OpInputFieldProjectionEntry,
    OpInputFieldProjection,
    OpInputMapModelProjection,
    OpInputListModelProjection
    > implements OpInputFormatReader {

  @Override
  protected GenProjectionsComparator<OpInputVarProjection, OpInputTagProjectionEntry, OpInputModelProjection<?, ?, ?, ?>, OpInputRecordModelProjection, OpInputMapModelProjection, OpInputListModelProjection, ?, OpInputFieldProjectionEntry, OpInputFieldProjection> projectionsComparator() {
    return new GenProjectionsComparator<>();
  }

  public OpInputJsonFormatReader(@NotNull JsonParser jsonParser) { super(jsonParser); }

  @Override
  protected boolean tagRequired(final @NotNull OpInputTagProjectionEntry tagProjection) {
    return tagProjection.projection().required();
  }

  @Override
  protected boolean fieldRequired(final @NotNull OpInputFieldProjectionEntry fieldEntry) {
    return fieldEntry.fieldProjection().required();
  }

  @Override
  protected @Nullable Set<Datum> getExpectedKeys(final @NotNull Collection<OpInputMapModelProjection> projections) {
    return null;
  }

  @Override
  protected @Nullable OpInputModelProjection<?, ?, ?, ?> getMetaProjection(final @NotNull OpInputModelProjection<?, ?, ?, ?> projection) {
    return projection.metaProjection();
  }

  public static class Factory implements FormatReader.Factory<OpInputJsonFormatReader> {
    @Override
    public @NotNull WireFormat format() { return JsonFormat.INSTANCE; }

    @Override
    public @NotNull OpInputJsonFormatReader newFormatReader(final @NotNull InputStream is) throws IOException {
      return new OpInputJsonFormatReader(AbstractJsonFormatReader.JSON_FACTORY.createParser(is));
    }
  }
}
