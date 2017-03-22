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

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.RecordModelProjectionHelper;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.types.RecordTypeApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

  private final @NotNull Map<String, ReqUpdateFieldProjectionEntry> fieldProjections;

  public ReqUpdateRecordModelProjection(
      @NotNull RecordTypeApi model,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull Map<String, ReqUpdateFieldProjectionEntry> fieldProjections,
      @Nullable List<ReqUpdateRecordModelProjection> tails,
      @NotNull TextLocation location) {
    super(model, update, params, annotations, tails, location);
    this.fieldProjections = fieldProjections;

    RecordModelProjectionHelper.checkFields(fieldProjections, model);
  }

  @Override
  public @NotNull Map<String, ReqUpdateFieldProjectionEntry> fieldProjections() { return fieldProjections; }

  @Override
  public @Nullable ReqUpdateFieldProjectionEntry fieldProjection(@NotNull String fieldName) {
    return fieldProjections.get(fieldName);
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
