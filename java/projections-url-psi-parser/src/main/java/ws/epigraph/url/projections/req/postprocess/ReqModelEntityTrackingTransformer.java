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

package ws.epigraph.url.projections.req.postprocess;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqModelProjection;
import ws.epigraph.projections.req.ReqProjectionTransformer;
import ws.epigraph.projections.req.ReqTagProjectionEntry;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TagApi;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqModelEntityTrackingTransformer extends ReqProjectionTransformer {
  protected final Map<ReqModelProjection<?, ?, ?>, EntityProjectionAndDataType> modelToEntity = new IdentityHashMap<>();

  @Override
  protected @NotNull ReqEntityProjection transformResolvedEntityProjection(
      final @NotNull ReqEntityProjection projection,
      final @Nullable DataTypeApi dataType,
      final @Nullable List<ReqEntityProjection> transformedTails,
      final boolean tailsChanged) {

    // build model -> entity index
    for (final Map.Entry<String, ReqTagProjectionEntry> entry : projection.tagProjections().entrySet()) {
      modelToEntity.put(entry.getValue().modelProjection(), new EntityProjectionAndDataType(projection, dataType));
    }

    return super.transformResolvedEntityProjection(projection, dataType, transformedTails, tailsChanged);
  }

  /** Flag model if it represents surrounding entity's default tag, and entity is flagged */
  protected boolean flagModel(@NotNull ReqModelProjection<?, ?, ?> modelProjection) {
    if (modelProjection.flag()) return false;
    else {
      EntityProjectionAndDataType epd = modelToEntity.get(modelProjection);
      if (epd == null || !epd.ep.flag()) return false;
      else {
        DataTypeApi dataType = epd.dataType;

        if (dataType == null) // nothing known about entity container type, can only guess
          return false;
        else {
          TagApi retroTag = dataType.retroTag();
          return retroTag != null && retroTag.type().equals(modelProjection.type());
        }
      }
    }
  }


  protected static final class EntityProjectionAndDataType {
    public final @NotNull ReqEntityProjection ep;
    public final @Nullable DataTypeApi dataType;

    private EntityProjectionAndDataType(
        final @NotNull ReqEntityProjection ep,
        final @Nullable DataTypeApi type) {

      this.ep = ep;
      dataType = type;
    }
  }
}
