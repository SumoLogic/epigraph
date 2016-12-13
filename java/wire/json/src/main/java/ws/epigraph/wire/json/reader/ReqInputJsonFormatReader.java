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
import ws.epigraph.data.Datum;
import ws.epigraph.projections.req.input.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JSON data reader guided by req output projection
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqInputJsonFormatReader extends AbstractJsonFormatReader<
    ReqInputVarProjection,
    ReqInputTagProjectionEntry,
    ReqInputModelProjection<?, ?>,
    ReqInputRecordModelProjection,
    ReqInputFieldProjectionEntry,
    ReqInputFieldProjection,
    ReqInputMapModelProjection,
    ReqInputListModelProjection
    > {

  public ReqInputJsonFormatReader(@NotNull JsonParser jsonParser) { super(jsonParser); }

  @Override
  protected boolean tagRequired(final @NotNull ReqInputTagProjectionEntry tagProjection) { return true; }

  @Override
  protected boolean fieldRequired(final @NotNull ReqInputFieldProjectionEntry fieldEntry) { return true; }

  @Override
  protected @Nullable Set<Datum> getExpectedKeys(
      final @NotNull Collection<@NotNull ? extends ReqInputMapModelProjection> projections
  ) {
    Set<Datum> expectedKeys = null;
    for (final ReqInputMapModelProjection projection : projections) {
      @Nullable Iterable<ReqInputKeyProjection> keyProjections = projection.keys();
      if (keyProjections == null) return null; // '*': all keys allowed
      if (expectedKeys == null) expectedKeys = new HashSet<>();
      for (ReqInputKeyProjection keyProjection : keyProjections) { expectedKeys.add(keyProjection.value()); }
    }
    return expectedKeys;
  }

}
