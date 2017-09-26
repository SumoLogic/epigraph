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

package ws.epigraph.projections.op.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.GRecordDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.RecordModelProjectionHelper;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpParams;
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
public class OpOutputRecordModelProjection
    extends OpOutputModelProjection<OpOutputModelProjection<?, ?, ?, ?>, OpOutputRecordModelProjection, RecordTypeApi, GRecordDatum>
    implements GenRecordModelProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?, ?>,
    OpOutputRecordModelProjection,
    OpOutputFieldProjectionEntry,
    OpOutputFieldProjection,
    RecordTypeApi
    > {

  private /*final*/ @NotNull Map<String, OpOutputFieldProjectionEntry> fieldProjections;

  public OpOutputRecordModelProjection(
      @NotNull RecordTypeApi model,
      boolean flagged,
      @Nullable GRecordDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull Map<String, OpOutputFieldProjectionEntry> fieldProjections,
      @Nullable List<OpOutputRecordModelProjection> tails,
      @NotNull TextLocation location) {

    super(model, flagged, defaultValue, params, annotations, metaProjection, tails, location);
    this.fieldProjections = Collections.unmodifiableMap(fieldProjections);

    RecordModelProjectionHelper.checkFields(fieldProjections, model);
  }

  public OpOutputRecordModelProjection(final @NotNull RecordTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
    this.fieldProjections = Collections.emptyMap();
  }

  @Override
  public @NotNull Map<String, OpOutputFieldProjectionEntry> fieldProjections() {
    assert isResolved();
    return fieldProjections;
  }

  @Override
  protected OpOutputRecordModelProjection clone() {
    if (isResolved) {
      return new OpOutputRecordModelProjection(
          model,
          flagged,
          defaultValue,
          params,
          annotations,
          metaProjection,
          fieldProjections,
          polymorphicTails,
          location()
      );
    } else {
      return new OpOutputRecordModelProjection(model, location());
    }
  }

  @Override
  protected OpOutputRecordModelProjection merge(
      final @NotNull RecordTypeApi model,
      final boolean mergedFlagged,
      final @Nullable GRecordDatum mergedDefault,
      final @NotNull List<OpOutputRecordModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpOutputModelProjection<?, ?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpOutputRecordModelProjection> mergedTails) {

    Map<FieldApi, OpOutputFieldProjection> mergedFieldProjections =
        RecordModelProjectionHelper.mergeFieldProjections(modelProjections);

    Map<String, OpOutputFieldProjectionEntry> mergedFieldEntries = new LinkedHashMap<>();
    for (final Map.Entry<FieldApi, OpOutputFieldProjection> entry : mergedFieldProjections.entrySet()) {
      mergedFieldEntries.put(
          entry.getKey().name(),
          new OpOutputFieldProjectionEntry(
              entry.getKey(),
              entry.getValue(),
              TextLocation.UNKNOWN
          )
      );
    }

    return new OpOutputRecordModelProjection(
        model,
        mergedFlagged,
        mergedDefault,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedFieldEntries,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull OpOutputRecordModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final @NotNull OpOutputRecordModelProjection n) {
    RecordTypeApi targetRecordType = (RecordTypeApi) targetType;

    final Map<String, OpOutputFieldProjection> normalizedFields =
        RecordModelProjectionHelper.normalizeFields(targetRecordType, n);

    final Map<String, OpOutputFieldProjectionEntry> normalizedFieldEntries = reattachFields(
        targetRecordType,
        normalizedFields,
        OpOutputFieldProjectionEntry::new
    );

    return new OpOutputRecordModelProjection(
        n.type(),
        n.flagged(),
        n.defaultValue(),
        n.params(),
        n.annotations(),
        n.metaProjection(),
        normalizedFieldEntries,
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(
      final @Nullable ProjectionReferenceName name,
      final @NotNull OpOutputRecordModelProjection value) {
    preResolveCheck(value);
    this.fieldProjections = value.fieldProjections();
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
