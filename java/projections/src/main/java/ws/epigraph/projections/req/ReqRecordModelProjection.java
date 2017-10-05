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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.RecordModelProjectionHelper;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.FieldApi;
import ws.epigraph.types.RecordTypeApi;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ws.epigraph.projections.RecordModelProjectionHelper.reattachFields;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqRecordModelProjection
    extends ReqModelProjection<ReqModelProjection<?, ?, ?>, ReqRecordModelProjection, RecordTypeApi>
    implements GenRecordModelProjection<
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>,
    ReqRecordModelProjection,
    ReqFieldProjectionEntry,
    ReqFieldProjection,
    RecordTypeApi
    > {

  private /*final*/ @NotNull Map<String, ReqFieldProjectionEntry> fieldProjections;

  public ReqRecordModelProjection(
      @NotNull RecordTypeApi model,
      boolean flag,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @Nullable ReqModelProjection<?, ?, ?> metaProjection,
      @NotNull Map<String, ReqFieldProjectionEntry> fieldProjections,
      @Nullable List<ReqRecordModelProjection> tails,
      @NotNull TextLocation location) {

    super(model, flag, params, directives, metaProjection, tails, location);
    this.fieldProjections = Collections.unmodifiableMap(fieldProjections);

    RecordModelProjectionHelper.checkFields(fieldProjections, model);
  }

  public ReqRecordModelProjection(final @NotNull RecordTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
    fieldProjections = Collections.emptyMap();
  }

  @Override
  public @NotNull Map<String, ReqFieldProjectionEntry> fieldProjections() {
    assert isResolved();
    return fieldProjections;
  }

  @Override
  public @Nullable ReqFieldProjectionEntry fieldProjection(@NotNull String fieldName) {
    return fieldProjections().get(fieldName);
  }

  @Override
  protected ReqRecordModelProjection clone() {
    if (isResolved()) {
      return new ReqRecordModelProjection(
          model, flag, params, directives, metaProjection, fieldProjections, polymorphicTails, location()
      );
    } else {
      return new ReqRecordModelProjection(model, location());
    }
  }

  @Override
  protected ReqRecordModelProjection merge(
      final @NotNull RecordTypeApi model,
      final boolean mergedFlag,
      final @NotNull List<ReqRecordModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqRecordModelProjection> mergedTails) {

    Map<FieldApi, ReqFieldProjection> mergedFieldProjections =
        RecordModelProjectionHelper.mergeFieldProjections(modelProjections);

    Map<String, ReqFieldProjectionEntry> mergedFieldEntries = new LinkedHashMap<>();
    for (final Map.Entry<FieldApi, ReqFieldProjection> entry : mergedFieldProjections.entrySet()) {
      mergedFieldEntries.put(
          entry.getKey().name(),
          new ReqFieldProjectionEntry(
              entry.getKey(),
              entry.getValue(),
              TextLocation.UNKNOWN
          )
      );
    }

    return new ReqRecordModelProjection(
        model,
        mergedFlag,
        mergedParams,
        mergedDirectives,
        mergedMetaProjection,
        mergedFieldEntries,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull ReqRecordModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull ReqRecordModelProjection n) {
    RecordTypeApi targetRecordType = (RecordTypeApi) targetType;

    final Map<String, ReqFieldProjection> normalizedFields =
        RecordModelProjectionHelper.normalizeFields(targetRecordType, n);

    final Map<String, ReqFieldProjectionEntry> normalizedFieldEntries = reattachFields(
        targetRecordType,
        normalizedFields,
        ReqFieldProjectionEntry::new
    );

    return new ReqRecordModelProjection(
        n.type(),
        n.flag(),
        n.params(),
        n.directives(),
        n.metaProjection(),
        normalizedFieldEntries,
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqRecordModelProjection value) {
    preResolveCheck(value);
    fieldProjections = value.fieldProjections();
    super.resolve(name, value);
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
