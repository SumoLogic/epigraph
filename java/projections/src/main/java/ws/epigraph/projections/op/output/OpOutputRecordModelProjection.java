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

package ws.epigraph.projections.op.output;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.RecordModelProjectionHelper;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.FieldApi;
import ws.epigraph.types.RecordTypeApi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputRecordModelProjection
    extends OpOutputModelProjection<OpOutputModelProjection<?, ?, ?>, OpOutputRecordModelProjection, RecordTypeApi>
    implements GenRecordModelProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?>,
    OpOutputRecordModelProjection,
    OpOutputFieldProjectionEntry,
    OpOutputFieldProjection,
    RecordTypeApi
    > {

  private final @NotNull Map<String, OpOutputFieldProjectionEntry> fieldProjections;

  public OpOutputRecordModelProjection(
      @NotNull RecordTypeApi model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputModelProjection<?, ?, ?> metaProjection,
      @NotNull Map<String, OpOutputFieldProjectionEntry> fieldProjections,
      @Nullable List<OpOutputRecordModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, tails, location);
    this.fieldProjections = fieldProjections;

    RecordModelProjectionHelper.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @Override
  public @NotNull Map<String, OpOutputFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  @Override
  protected OpOutputRecordModelProjection merge(
      final @NotNull RecordTypeApi model,
      final @NotNull List<OpOutputRecordModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpOutputModelProjection<?, ?, ?> mergedMetaProjection,
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
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedFieldEntries,
        mergedTails,
        TextLocation.UNKNOWN
    );
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
