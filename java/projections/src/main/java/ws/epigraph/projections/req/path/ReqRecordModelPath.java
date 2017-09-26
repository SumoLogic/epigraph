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

package ws.epigraph.projections.req.path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.req.Directives;
import ws.epigraph.projections.RecordModelProjectionHelper;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.RecordTypeApi;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqRecordModelPath
    extends ReqModelPath<ReqModelPath<?, ?, ?>, ReqRecordModelPath, RecordTypeApi>
    implements GenRecordModelProjection<
    ReqVarPath,
    ReqTagPath,
    ReqModelPath<?, ?, ?>,
    ReqRecordModelPath,
    ReqFieldPathEntry,
    ReqFieldPath,
    RecordTypeApi
    > {

  private final @NotNull Map<String, ReqFieldPathEntry> fieldProjections;

  public ReqRecordModelPath(
      @NotNull RecordTypeApi model,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull ReqFieldPathEntry fieldProjection,
      @NotNull TextLocation location) {
    super(model, params, directives, location);
    this.fieldProjections = Collections.singletonMap(fieldProjection.field().name(), fieldProjection);

    RecordModelProjectionHelper.checkFields(fieldProjections, model);

    if (pathFieldProjection() == null) throw new IllegalArgumentException("Path field must be present");
  }

  @Override
  public @NotNull Map<String, ReqFieldPathEntry> fieldProjections() { return fieldProjections; }

  @Override
  public @Nullable ReqFieldPathEntry fieldProjection(@NotNull String fieldName) {
    return fieldProjections.get(fieldName);
  }

  @Override
  protected ReqRecordModelPath clone() {
    return new ReqRecordModelPath(
        model,
        params,
        directives,
        fieldProjections.entrySet().iterator().next().getValue(),
        location()
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
