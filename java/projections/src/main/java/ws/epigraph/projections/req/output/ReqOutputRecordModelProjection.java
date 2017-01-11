/*
 * CReqyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a cReqy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.projections.req.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.RecordModelProjectionHelper;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.FieldApi;
import ws.epigraph.types.RecordTypeApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputRecordModelProjection
    extends ReqOutputModelProjection<ReqOutputModelProjection<?, ?, ?>, ReqOutputRecordModelProjection, RecordTypeApi>
    implements GenRecordModelProjection<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>,
    ReqOutputRecordModelProjection,
    ReqOutputFieldProjectionEntry,
    ReqOutputFieldProjection,
    RecordTypeApi
    > {

  private final @NotNull Map<String, ReqOutputFieldProjectionEntry> fieldProjections;

  public ReqOutputRecordModelProjection(
      @NotNull RecordTypeApi model,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputModelProjection<?, ?, ?> metaProjection,
      @NotNull Map<String, ReqOutputFieldProjectionEntry> fieldProjections,
      @NotNull TextLocation location) {
    super(model, required, params, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    RecordModelProjectionHelper.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @Override
  public @NotNull Map<String, ReqOutputFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  @Override
  public @Nullable ReqOutputFieldProjectionEntry fieldProjection(@NotNull String fieldName) {
    return fieldProjections.get(fieldName);
  }

  @Override
  protected ReqOutputRecordModelProjection merge(
      final @NotNull RecordTypeApi model,
      final boolean mergedRequired,
      final @NotNull List<ReqOutputRecordModelProjection> modelProjections,
      final @NotNull ReqParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable ReqOutputModelProjection<?, ?, ?> mergedMetaProjection) {


    Map<FieldApi, ReqOutputFieldProjection> mergedFieldProjections =
        RecordModelProjectionHelper.mergeFieldProjections(modelProjections);

    Map<String, ReqOutputFieldProjectionEntry> mergedFieldEntries = new LinkedHashMap<>();
    for (final Map.Entry<FieldApi, ReqOutputFieldProjection> entry : mergedFieldProjections.entrySet()) {
      mergedFieldEntries.put(
          entry.getKey().name(),
          new ReqOutputFieldProjectionEntry(
              entry.getKey(),
              entry.getValue(),
              TextLocation.UNKNOWN
          )
      );
    }

    return new ReqOutputRecordModelProjection(
        model,
        mergedRequired,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedFieldEntries,
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
