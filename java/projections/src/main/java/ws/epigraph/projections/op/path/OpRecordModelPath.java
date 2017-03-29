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

package ws.epigraph.projections.op.path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.RecordModelProjectionHelper;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.RecordTypeApi;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpRecordModelPath
    extends OpModelPath<OpModelPath<?, ?, ?>, OpRecordModelPath, RecordTypeApi>
    implements GenRecordModelProjection<
    OpVarPath,
    OpTagPath,
    OpModelPath<?, ?, ?>,
    OpRecordModelPath,
    OpFieldPathEntry,
    OpFieldPath,
    RecordTypeApi
    > {

  private final @NotNull Map<String, OpFieldPathEntry> fieldProjections;
  private @Nullable OpFieldPathEntry fieldPathEntry;

  public OpRecordModelPath(
      @NotNull RecordTypeApi model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpFieldPathEntry fieldPathEntry,
      @NotNull TextLocation location) {
    super(model, params, annotations, location);

    this.fieldPathEntry = fieldPathEntry;

    this.fieldProjections = fieldPathEntry == null ?
                            Collections.emptyMap() :
                            Collections.singletonMap(fieldPathEntry.field().name(), fieldPathEntry);

    RecordModelProjectionHelper.checkFields(fieldProjections, model);

    if (pathFieldProjection() == null) throw new IllegalArgumentException("Path field must be present");
  }

  @Override
  public @NotNull Map<String, OpFieldPathEntry> fieldProjections() { return fieldProjections; }

  public @Nullable OpFieldPathEntry fieldPathEntry() { return fieldPathEntry; }

  @Override
  public boolean equals(Object o) {
    return super.equals(o) && RecordModelProjectionHelper.equals(this, o);
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 31 + fieldProjections.size();
  }
}
