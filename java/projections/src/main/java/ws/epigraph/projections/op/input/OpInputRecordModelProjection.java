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

package ws.epigraph.projections.op.input;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.GRecordDatum;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.RecordModelProjectionHelper;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.RecordTypeApi;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputRecordModelProjection
    extends OpInputModelProjection<OpInputModelProjection<?, ?, ?, ?>, OpInputRecordModelProjection, RecordTypeApi, GRecordDatum>
    implements GenRecordModelProjection<
    OpInputVarProjection,
    OpInputTagProjectionEntry,
    OpInputModelProjection<?, ?, ?, ?>,
    OpInputRecordModelProjection,
    OpInputFieldProjectionEntry,
    OpInputFieldProjection,
    RecordTypeApi
    > {

  private /*final*/ @NotNull Map<String, OpInputFieldProjectionEntry> fieldProjections;

  public OpInputRecordModelProjection(
      @NotNull RecordTypeApi model,
      boolean required,
      @Nullable GRecordDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull Map<String, OpInputFieldProjectionEntry> fieldProjections,
      @Nullable List<OpInputRecordModelProjection> tails,
      @NotNull TextLocation location) {

    super(model, required, defaultValue, params, annotations, metaProjection, tails, location);
    this.fieldProjections = fieldProjections;

    RecordModelProjectionHelper.checkFields(fieldProjections, model);
  }

  public OpInputRecordModelProjection(final @NotNull RecordTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
    this.fieldProjections = Collections.emptyMap();
  }

  @Override
  public @NotNull OpInputRecordModelProjection newReference(
      final @NotNull RecordTypeApi type,
      final @NotNull TextLocation location) {
    return new OpInputRecordModelProjection(type, location);
  }

  public static @NotNull LinkedHashSet<OpInputFieldProjectionEntry> fields(OpInputFieldProjectionEntry... fieldProjections) {
    return new LinkedHashSet<>(Arrays.asList(fieldProjections));
  }

  @Override
  public @NotNull Map<String, OpInputFieldProjectionEntry> fieldProjections() {
    assert isResolved();
    return fieldProjections;
  }

  @Override
  public void resolve(final @Nullable Qn name, final @NotNull OpInputRecordModelProjection value) {
    super.resolve(name, value);
    this.fieldProjections = value.fieldProjections();
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o) && RecordModelProjectionHelper.equals(this, o);
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 31 + fieldProjections.size();
  }
}
