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
import ws.epigraph.projections.op.input.*;

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
    OpInputModelProjection<?, ?, ?>,
    OpInputRecordModelProjection,
    OpInputFieldProjectionEntry,
    OpInputFieldProjection,
    OpInputMapModelProjection,
    OpInputListModelProjection
    > {

  public OpInputJsonFormatReader(@NotNull JsonParser jsonParser) { super(jsonParser); }

  @Override
  protected boolean tagRequired(@NotNull final OpInputTagProjectionEntry tagProjection) {
    return tagProjection.projection().required();
  }

  @Override
  protected boolean fieldRequired(@NotNull final OpInputFieldProjectionEntry fieldEntry) {
    return fieldEntry.fieldProjection().required();
  }

  @Nullable
  @Override
  protected String monoTag(@NotNull final Iterable<? extends OpInputVarProjection> projections) { return null; }

  @Nullable
  @Override
  protected Set<Datum> getExpectedKeys(@NotNull final Collection<? extends OpInputMapModelProjection> projections) {
    return null;
  }

}
