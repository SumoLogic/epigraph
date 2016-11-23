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
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.RecordType;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputRecordModelProjection
    extends OpOutputModelProjection<OpOutputRecordModelProjection, RecordType>
    implements GenRecordModelProjection<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?>,
    OpOutputRecordModelProjection,
    OpOutputFieldProjectionEntry,
    OpOutputFieldProjection,
    RecordType
    > {

  @NotNull
  private Map<String, OpOutputFieldProjectionEntry> fieldProjections;

  public OpOutputRecordModelProjection(
      @NotNull RecordType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputRecordModelProjection metaProjection,
      @NotNull Map<String, OpOutputFieldProjectionEntry> fieldProjections,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
    this.fieldProjections = fieldProjections;

    RecordModelProjectionHelper.checkFieldsBelongsToModel(fieldProjections.keySet(), model);
  }

  @NotNull
  public Map<String, OpOutputFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  /*
  @Override
  protected OpOutputRecordModelProjection merge(
      @NotNull final DatumType model,
      @NotNull final List<? extends GenModelProjection<?, ?>> modelProjections,
      @NotNull final OpParams mergedParams,
      @NotNull final Annotations mergedAnnotations,
      @Nullable final OpOutputRecordModelProjection mergedMetaProjection) {

    Set<RecordType.Field> collectedFields = new LinkedHashSet<>();
    for (final GenModelProjection<?, ?> projection : modelProjections) {

    }


    Map<String, OpOutputFieldProjectionEntry> mergedFields = new LinkedHashMap<>();

  }
  */

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) return false;
    return RecordModelProjectionHelper.equals(this, o);
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 31 + fieldProjections.size();
  }
}
