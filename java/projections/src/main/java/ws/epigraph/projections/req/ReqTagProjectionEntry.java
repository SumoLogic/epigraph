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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.types.TagApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqTagProjectionEntry
    extends AbstractTagProjectionEntry<ReqTagProjectionEntry, ReqModelProjection<?, ?, ?>> {

  public ReqTagProjectionEntry(
      @NotNull TagApi tag,
      @NotNull ReqModelProjection<?, ?, ?> projection,
      @NotNull TextLocation location) {
    super(tag, projection, location);
  }

  @Override
  protected @NotNull ReqTagProjectionEntry mergeTags(
      final @NotNull TagApi tag,
      final @NotNull List<ReqTagProjectionEntry> tagsEntries,
      final @NotNull ReqModelProjection<?, ?, ?> mergedModel) {

    return new ReqTagProjectionEntry(
        tag,
        mergedModel,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull ReqTagProjectionEntry setModelProjection(final @NotNull ReqModelProjection<?, ?, ?> modelProjection) {
    return new ReqTagProjectionEntry(tag(), modelProjection, TextLocation.UNKNOWN);
  }
}
