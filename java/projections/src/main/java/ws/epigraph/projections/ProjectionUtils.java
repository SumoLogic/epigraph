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
import java.util.function.Function;
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

      final GenModelProjection<?, ?, ?, ?> modelPath = tagProjection.projection();
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

    outer:
    while (true) {
      final GenTagProjectionEntry<?, ?> tagProjection = path.singleTagProjection();
      if (tagProjection == null) break;

      len++;

      final GenModelProjection<?, ?, ?, ?> modelPath = tagProjection.projection();
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
          break outer;
      }
    }

    return len;
  }

  public static @NotNull TypeApi mostSpecific(@NotNull TypeApi t1, @NotNull TypeApi t2) {
    if (t1.isAssignableFrom(t2)) return t1;
    if (t2.isAssignableFrom(t1)) return t2;
    throw new IllegalArgumentException(
        String.format("Types '%s' and '%s' are not related", t1.name().toString(), t2.name())
    );
  }

  public static @NotNull DatumTypeApi mostSpecific(@NotNull DatumTypeApi t1, @NotNull DatumTypeApi t2) {
    if (t1.isAssignableFrom(t2)) return t1;
    if (t2.isAssignableFrom(t1)) return t2;
    throw new IllegalArgumentException(
        String.format("Types '%s' and '%s' are not related", t1.name().toString(), t2.name())
    );
  }

  // var tails linearization

  public static <VP extends GenVarProjection<VP, ?, ?>> List<VP> linearizeVarTails(
      @NotNull TypeApi t,
      @NotNull Stream<VP> tails) {

    return linearizeVarTails(t, tails, new LinkedList<>());
  }

  public static <VP extends GenVarProjection<VP, ?, ?>> List<VP> linearizeVarTails(
      @NotNull TypeApi t,
      @NotNull List<VP> tails) {

    return linearizeTails(
        VP::type,
        VP::polymorphicTails,
        t,
        tails
    );

  }

  public static <VP extends GenVarProjection<VP, ?, ?>> List<VP> linearizeVarTails(
      @NotNull TypeApi type,
      @NotNull Stream<VP> tails,
      @NotNull LinkedList<VP> linearizedTails) {

    return linearizeTails(
        VP::type,
        VP::polymorphicTails,
        type,
        tails,
        linearizedTails
    );
  }

  // model tails linearization

  public static <MP extends GenModelProjection<?, ?, MP, ?>> List<MP> linearizeModelTails(
      @NotNull TypeApi t,
      @NotNull Stream<MP> tails) {

    return linearizeModelTails(t, tails, new LinkedList<>());
  }

  public static <MP extends GenModelProjection<?, ?, MP, ?>> List<MP> linearizeModelTails(
      @NotNull TypeApi t,
      @NotNull List<MP> tails) {

    return linearizeTails(
        m -> m.model(),
        MP::polymorphicTails,
        t,
        tails
    );

  }

  public static <MP extends GenModelProjection<?, ?, MP, ?>> List<MP> linearizeModelTails(
      @NotNull TypeApi type,
      @NotNull Stream<MP> tails,
      @NotNull LinkedList<MP> linearizedTails) {

    return linearizeTails(
        m -> m.model(),
        MP::polymorphicTails,
        type,
        tails,
        linearizedTails
    );
  }

  // generic tails linearization

  private static <P> List<P> linearizeTails(
      @NotNull Function<P, TypeApi> typeAccessor,
      @NotNull Function<P, List<P>> tailsAccessor,
      @NotNull TypeApi t,
      @NotNull List<P> tails) {

    if (tails.isEmpty()) return Collections.emptyList();
    if (tails.size() == 1) {
      final P tail = tails.get(0);
      final Collection<P> tailTails = tailsAccessor.apply(tail);

      if (typeAccessor.apply(tail).isAssignableFrom(t)) {
        if (tailTails == null || tailTails.isEmpty())
          return tails;
        // else run full linearizeTails below
      } else
        return Collections.emptyList();
    }

    return linearizeTails(typeAccessor, tailsAccessor, t, tails.stream(), new LinkedList<>());
  }

  private static <P> List<P> linearizeTails(
      @NotNull Function<P, TypeApi> typeAccessor,
      @NotNull Function<P, List<P>> tailsAccessor,
      @NotNull TypeApi type,
      @NotNull Stream<P> tails,
      @NotNull LinkedList<P> linearizedTails) {

    final Optional<P> matchingTailOpt =
        tails.filter(tail -> typeAccessor.apply(tail).isAssignableFrom(type)).findFirst();

    if (matchingTailOpt.isPresent()) {
      final P matchingTail = matchingTailOpt.get();
      linearizedTails.addFirst(matchingTail);
//      linearizedTails.addFirst(stripTails(matchingTail));

      final List<P> tails2 = tailsAccessor.apply(matchingTail);
      if (tails2 != null)
        linearizeTails(typeAccessor, tailsAccessor, type, tails2.stream(), linearizedTails);

    }

    return linearizedTails;
  }
}
