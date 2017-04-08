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

package ws.epigraph.projections.op.delete;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
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
public class OpDeleteRecordModelProjection
    extends OpDeleteModelProjection<OpDeleteModelProjection<?, ?, ?>, OpDeleteRecordModelProjection, RecordTypeApi>
    implements GenRecordModelProjection<
    OpDeleteVarProjection,
    OpDeleteTagProjectionEntry,
    OpDeleteModelProjection<?, ?, ?>,
    OpDeleteRecordModelProjection,
    OpDeleteFieldProjectionEntry,
    OpDeleteFieldProjection,
    RecordTypeApi
    > {

  private /*final*/ @NotNull Map<String, OpDeleteFieldProjectionEntry> fieldProjections;

  public OpDeleteRecordModelProjection(
      @NotNull RecordTypeApi model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull Map<String, OpDeleteFieldProjectionEntry> fieldProjections,
      @Nullable List<OpDeleteRecordModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, annotations, tails, location);
    this.fieldProjections = fieldProjections;

    RecordModelProjectionHelper.checkFields(fieldProjections, model);
  }

  protected OpDeleteRecordModelProjection(final @NotNull RecordTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
    this.fieldProjections = Collections.emptyMap();
  }

  @Override
  public @NotNull Map<String, OpDeleteFieldProjectionEntry> fieldProjections() {
    assert isResolved();
    return fieldProjections;
  }
  
  @Override
  protected OpDeleteRecordModelProjection merge(
      final @NotNull RecordTypeApi model,
      final @NotNull List<OpDeleteRecordModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpDeleteModelProjection<?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpDeleteRecordModelProjection> mergedTails,
      final boolean keepPhantomTails) {

    Map<FieldApi, OpDeleteFieldProjection> mergedFieldProjections =
        RecordModelProjectionHelper.mergeFieldProjections(modelProjections, keepPhantomTails);

    Map<String, OpDeleteFieldProjectionEntry> mergedFieldEntries = new LinkedHashMap<>();
    for (final Map.Entry<FieldApi, OpDeleteFieldProjection> entry : mergedFieldProjections.entrySet()) {
      mergedFieldEntries.put(
          entry.getKey().name(),
          new OpDeleteFieldProjectionEntry(
              entry.getKey(),
              entry.getValue(),
              TextLocation.UNKNOWN
          )
      );
    }

    return new OpDeleteRecordModelProjection(
        model,
        mergedParams,
        mergedAnnotations,
        mergedFieldEntries,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull OpDeleteRecordModelProjection postNormalizedForType(
      final @NotNull DatumTypeApi targetType,
      final boolean keepPhantomTails,
      final @NotNull OpDeleteRecordModelProjection n) {
    RecordTypeApi targetRecordType = (RecordTypeApi) targetType;

    final Map<String, OpDeleteFieldProjection> normalizedFields =
        RecordModelProjectionHelper.normalizeFields(targetRecordType, n, keepPhantomTails);

    final Map<String, OpDeleteFieldProjectionEntry> normalizedFieldEntries = reattachFields(
        targetRecordType,
        normalizedFields,
        OpDeleteFieldProjectionEntry::new
    );

    return new OpDeleteRecordModelProjection(
        n.type(),
        n.params(),
        n.annotations(),
        normalizedFieldEntries,
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(final @Nullable ProjectionReferenceName name, final @NotNull OpDeleteRecordModelProjection value) {
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
