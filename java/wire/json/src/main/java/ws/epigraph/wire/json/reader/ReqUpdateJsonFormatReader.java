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
import ws.epigraph.projections.req.update.*;
import ws.epigraph.wire.FormatReader;
import ws.epigraph.wire.ReqUpdateFormatReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * JSON data reader guided by req output projection
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateJsonFormatReader extends AbstractJsonFormatReader<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?, ?>,
    ReqUpdateRecordModelProjection,
    ReqUpdateFieldProjectionEntry,
    ReqUpdateFieldProjection,
    ReqUpdateMapModelProjection,
    ReqUpdateListModelProjection
    > implements ReqUpdateFormatReader {

  public ReqUpdateJsonFormatReader(@NotNull JsonParser jsonParser) { super(jsonParser); }

  @Override
  protected GenProjectionsComparator<ReqUpdateVarProjection, ReqUpdateTagProjectionEntry, ReqUpdateModelProjection<?, ?, ?>, ReqUpdateRecordModelProjection, ReqUpdateMapModelProjection, ReqUpdateListModelProjection, ?, ReqUpdateFieldProjectionEntry, ReqUpdateFieldProjection> projectionsComparator() {
    return new GenProjectionsComparator<>();
  }

//  @Override
//  protected boolean tagRequired(@NotNull ReqUpdateTagProjectionEntry tagProjection) { return true; }
//
//  @Override
//  protected boolean fieldRequired(@NotNull ReqUpdateFieldProjectionEntry fieldEntry) { return true; }

  @Override
  protected @Nullable Set<Datum> getExpectedKeys(
      @NotNull Collection<@NotNull ReqUpdateMapModelProjection> projections
  ) {
    Set<Datum> expectedKeys = null;
    for (final ReqUpdateMapModelProjection projection : projections) {
      final @Nullable Iterable<ReqUpdateKeyProjection> keyProjections = projection.keys();
      if (expectedKeys == null) expectedKeys = new HashSet<>();
      for (ReqUpdateKeyProjection keyProjection : keyProjections) { expectedKeys.add(keyProjection.value()); }
    }
    return expectedKeys;
  }
  
  public static class Factory implements FormatReader.Factory<ReqUpdateJsonFormatReader> {
    @Override
    public @NotNull ReqUpdateJsonFormatReader newFormatReader(final @NotNull InputStream is) throws IOException {
      return new ReqUpdateJsonFormatReader(AbstractJsonFormatReader.JSON_FACTORY.createParser(is));
    }
  }

}
