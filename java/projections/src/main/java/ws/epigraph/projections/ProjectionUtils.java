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

package ws.epigraph.projections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionUtils {
  @NotNull
  public static <K, V> LinkedHashMap<K, V> singletonLinkedHashMap(@NotNull K key, @NotNull V value) {
    final LinkedHashMap<K, V> res = new LinkedHashMap<>();
    res.put(key, value);
    return res;
  }

  @NotNull
  public static String listFields(@Nullable Collection<String> fieldNames) {
    if (fieldNames == null) return "<none>";
    return String.join(",", fieldNames);
  }

  /**
   * @return {@code path} tip type
   */
  @NotNull
  public static DataType tipType(@NotNull GenVarProjection<?, ?, ?> path) {
    DataType lastDataType;

    final Type type = path.type();
    if (type instanceof DatumType) {
      DatumType datumType = (DatumType) type;
      lastDataType = datumType.dataType();
    } else {
      lastDataType = new DataType(type, null);
    }

    while (true) {
      final GenTagProjectionEntry<?, ?> tagProjection = path.pathTagProjection();
      if (tagProjection == null) break;

      lastDataType = tagProjection.tag().type.dataType();

      final GenModelProjection<?, ?> modelPath = tagProjection.projection();
      final DatumType model = modelPath.model();
      switch (model.kind()) {
        case RECORD:
          GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?> recordPath =
              (GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?>) modelPath;

          GenFieldProjectionEntry<?, ?, ?, ?> fieldProjection = recordPath.pathFieldProjection();
          if (fieldProjection == null) break;
          lastDataType = fieldProjection.field().dataType();
          path = fieldProjection.fieldProjection().varProjection();
          break;
        case MAP:
          GenMapModelProjection<?, ?, ?, ?, ?> mapPath = (GenMapModelProjection<?, ?, ?, ?, ?>) modelPath;

          lastDataType = mapPath.model().valueType();
          path = mapPath.itemsProjection();
          break;
        default:
          break;
      }
    }

    return lastDataType;
  }

  public static int pathLength(@NotNull GenVarProjection<?, ?, ?> path) {
    int len = 0;

    while (true) {
      final GenTagProjectionEntry<?, ?> tagProjection = path.pathTagProjection();
      if (tagProjection == null) break;

      len++;

      final GenModelProjection<?, ?> modelPath = tagProjection.projection();
      final DatumType model = modelPath.model();
      switch (model.kind()) {
        case RECORD:
          GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?> recordPath =
              (GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?>) modelPath;

          GenFieldProjectionEntry<?, ?, ?, ?> fieldProjection = recordPath.pathFieldProjection();
          if (fieldProjection == null) break;
          len++;
          path = fieldProjection.fieldProjection().varProjection();
          break;
        case MAP:
          GenMapModelProjection<?, ?, ?, ?, ?> mapPath = (GenMapModelProjection<?, ?, ?, ?, ?>) modelPath;
          len++;
          path = mapPath.itemsProjection();
          break;
        default:
          break;
      }
    }

    return len;
  }
}
