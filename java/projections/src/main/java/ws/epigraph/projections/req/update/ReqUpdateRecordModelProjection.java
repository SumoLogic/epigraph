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

package ws.epigraph.projections.req.update;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.req.Directives;
import ws.epigraph.projections.RecordModelProjectionHelper;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.ReqParams;
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
public class ReqUpdateRecordModelProjection
    extends ReqUpdateModelProjection<ReqUpdateModelProjection<?, ?, ?>, ReqUpdateRecordModelProjection, RecordTypeApi>
    implements GenRecordModelProjection<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?, ?>,
    ReqUpdateRecordModelProjection,
    ReqUpdateFieldProjectionEntry,
    ReqUpdateFieldProjection,
    RecordTypeApi
    > {

  private /*final*/ @NotNull Map<String, ReqUpdateFieldProjectionEntry> fieldProjections;

  public ReqUpdateRecordModelProjection(
      @NotNull RecordTypeApi model,
      boolean replace,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull Map<String, ReqUpdateFieldProjectionEntry> fieldProjections,
      @Nullable List<ReqUpdateRecordModelProjection> tails,
      @NotNull TextLocation location) {

    super(model, replace, params, directives, tails, location);
    this.fieldProjections = Collections.unmodifiableMap(fieldProjections);

    RecordModelProjectionHelper.checkFields(fieldProjections, model);
  }

  public ReqUpdateRecordModelProjection(final @NotNull RecordTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
    fieldProjections = Collections.emptyMap();
  }

  @Override
  public @NotNull Map<String, ReqUpdateFieldProjectionEntry> fieldProjections() {
    assert isResolved();
    return fieldProjections;
  }

  @Override
  public @Nullable ReqUpdateFieldProjectionEntry fieldProjection(@NotNull String fieldName) {
    assert isResolved();
    return fieldProjections.get(fieldName);
  }

  @Override
  protected ReqUpdateRecordModelProjection merge(
      final @NotNull RecordTypeApi model,
      final boolean mergedUpdate,
      final @NotNull List<ReqUpdateRecordModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Directives mergedDirectives,
      final @Nullable ReqUpdateModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<ReqUpdateRecordModelProjection> mergedTails) {

    Map<FieldApi, ReqUpdateFieldProjection> mergedFieldProjections =
        RecordModelProjectionHelper.mergeFieldProjections(modelProjections);

    Map<String, ReqUpdateFieldProjectionEntry> mergedFieldEntries = new LinkedHashMap<>();
    for (final Map.Entry<FieldApi, ReqUpdateFieldProjection> entry : mergedFieldProjections.entrySet()) {
      mergedFieldEntries.put(
          entry.getKey().name(),
          new ReqUpdateFieldProjectionEntry(
              entry.getKey(),
              entry.getValue(),
              TextLocation.UNKNOWN
          )
      );
    }

    return new ReqUpdateRecordModelProjection(
        model,
        mergedUpdate,
        mergedParams,
        mergedDirectives,
        mergedFieldEntries,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  protected @NotNull ReqUpdateRecordModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull ReqUpdateRecordModelProjection n) {

    RecordTypeApi targetRecordType = (RecordTypeApi) targetType;

    final Map<String, ReqUpdateFieldProjection> normalizedFields =
        RecordModelProjectionHelper.normalizeFields(targetRecordType, n);

    final Map<String, ReqUpdateFieldProjectionEntry> normalizedFieldEntries = reattachFields(
        targetRecordType,
        normalizedFields,
        ReqUpdateFieldProjectionEntry::new
    );

    return new ReqUpdateRecordModelProjection(
        n.type(),
        n.replace(),
        n.params(),
        n.directives(),
        normalizedFieldEntries,
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull ReqUpdateRecordModelProjection value) {
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
