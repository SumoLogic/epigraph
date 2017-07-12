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
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.types.TagApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateTagProjectionEntry
    extends AbstractTagProjectionEntry<ReqUpdateTagProjectionEntry, ReqUpdateModelProjection<?, ?, ?>> {

  public ReqUpdateTagProjectionEntry(
      @NotNull TagApi tag,
      @NotNull ReqUpdateModelProjection<?, ?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }

  @Override
  protected @NotNull ReqUpdateTagProjectionEntry mergeTags(
      final @NotNull TagApi tag,
      final @NotNull List<ReqUpdateTagProjectionEntry> tagsEntries,
      final @NotNull ReqUpdateModelProjection<?, ?, ?> mergedModel) {

    return new ReqUpdateTagProjectionEntry(
        tag,
        mergedModel,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull ReqUpdateTagProjectionEntry setModelProjection(final @NotNull ReqUpdateModelProjection<?, ?, ?> modelProjection) {
    return new ReqUpdateTagProjectionEntry(tag(), modelProjection, TextLocation.UNKNOWN);
  }
}
