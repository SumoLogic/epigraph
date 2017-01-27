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
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.types.UnionTypeApi;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ProjectionUtils {
  private ProjectionUtils() {}

  public static @NotNull <K, V> LinkedHashMap<K, V> singletonLinkedHashMap(@NotNull K key, @NotNull V value) {
    final LinkedHashMap<K, V> res = new LinkedHashMap<>();
    res.put(key, value);
    return res;
  }

  public static @NotNull String listFields(@Nullable Collection<String> fieldNames) {
    if (fieldNames == null) return "<none>";
    return String.join(",", fieldNames);
  }

  /**
   * @return {@code path} tip type
   */
  public static @NotNull DataTypeApi tipType(@NotNull GenVarProjection<?, ?, ?> path) {
    DataTypeApi lastDataType;

    final TypeApi type = path.type();
    if (type instanceof DatumTypeApi) {
      DatumTypeApi datumType = (DatumTypeApi) type;
      lastDataType = datumType.dataType();
    } else {
      lastDataType = ((UnionTypeApi) type).dataType(null);
    }

    while (true) {
      final GenTagProjectionEntry<?, ?> tagProjection = path.singleTagProjection();
      if (tagProjection == null) break;

      lastDataType = tagProjection.tag().type().dataType();

      final GenModelProjection<?, ?, ?> modelPath = tagProjection.projection();
      final DatumTypeApi model = modelPath.model();
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
      final GenTagProjectionEntry<?, ?> tagProjection = path.singleTagProjection();
      if (tagProjection == null) break;

      len++;

      final GenModelProjection<?, ?, ?> modelPath = tagProjection.projection();
      final DatumTypeApi model = modelPath.model();
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

  public static <VP extends GenVarProjection<VP, ?, ?>> List<VP> linearizeTails(
      @NotNull TypeApi t,
      @NotNull Stream<VP> tails) {

    return linearizeTails(t, tails, new LinkedList<>());
  }

  public static <VP extends GenVarProjection<VP, ?, ?>> List<VP> linearizeTails(
      @NotNull TypeApi t,
      @NotNull List<VP> tails) {

    if (tails.isEmpty()) return Collections.emptyList();
    if (tails.size() == 1) {
      final VP tail = tails.get(0);
      final Collection<VP> tailTails = tail.polymorphicTails();

      if (tail.type().isAssignableFrom(t)) {
        if (tailTails == null || tailTails.isEmpty())
          return tails;
        // else run full linearizeTails below
      } else
        return Collections.emptyList();
    }

    return linearizeTails(t, tails.stream(), new LinkedList<>());
  }

  public static <VP extends GenVarProjection<VP, ?, ?>> List<VP> linearizeTails(
      @NotNull TypeApi type,
      @NotNull Stream<VP> tails,
      @NotNull LinkedList<VP> linearizedTails) {

    final Optional<VP> matchingTailOpt = tails.filter(tail -> tail.type().isAssignableFrom(type)).findFirst();

    if (matchingTailOpt.isPresent()) {
      final VP matchingTail = matchingTailOpt.get();
      linearizedTails.addFirst(matchingTail);
//      linearizedTails.addFirst(stripTails(matchingTail));

      final List<VP> tails2 = matchingTail.polymorphicTails();
      if (tails2 != null)
        linearizeTails(type, tails2.stream(), linearizedTails);

    }

    return linearizedTails;
  }
}
