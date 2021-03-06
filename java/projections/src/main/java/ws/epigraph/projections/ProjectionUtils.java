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

package ws.epigraph.projections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.names.AnonListTypeName;
import ws.epigraph.names.AnonMapTypeName;
import ws.epigraph.names.QualifiedTypeName;
import ws.epigraph.names.TypeName;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.*;

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

  public static @NotNull String listStrings(@Nullable Collection<String> fieldNames) {
    if (fieldNames == null) return "<none>";
    return String.join(",", fieldNames);
  }

  /**
   * @return {@code path} tip type
   */
  public static @NotNull DataTypeApi tipType(@NotNull GenProjection<?, ?, ?, ?> path) {
    DataTypeApi lastDataType;

    final TypeApi type = path.type();
    if (type instanceof DatumTypeApi) {
      DatumTypeApi datumType = (DatumTypeApi) type;
      lastDataType = datumType.dataType();
    } else {
      lastDataType = ((EntityTypeApi) type).dataType(null);
    }

    while (!path.isPathEnd()) {
      final GenTagProjectionEntry<?, ?> tagProjection = path.singleTagProjection();
      if (tagProjection == null) break;

      lastDataType = tagProjection.tag().type().dataType();

      final GenModelProjection<?, ?, ?, ?, ?> modelPath = tagProjection.modelProjection();
      final TypeApi model = modelPath.type();
      switch (model.kind()) {
        case RECORD:
          GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?, ?> recordPath =
              (GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?, ?>) modelPath;

          GenFieldProjectionEntry<?, ?, ?, ?> fieldProjection = recordPath.pathFieldProjection();
          if (fieldProjection == null) break;
          lastDataType = fieldProjection.field().dataType();
          path = fieldProjection.fieldProjection().projection();
          break;
        case MAP:
          GenMapModelProjection<?, ?, ?, ?, ?, ?> mapPath = (GenMapModelProjection<?, ?, ?, ?, ?, ?>) modelPath;

          lastDataType = mapPath.type().valueType();
          path = mapPath.itemsProjection();
          break;
        default:
          break;
      }
    }

    return lastDataType;
  }

  public static int pathLength(@NotNull GenProjection<?, ?, ?, ?> path) {
    int len = 0;

    outer:
    while (true) {
      final GenTagProjectionEntry<?, ?> tagProjection = path.singleTagProjection();
      if (tagProjection == null) break;

      len++;

      final GenModelProjection<?, ?, ?, ?, ?> modelPath = tagProjection.modelProjection();
      final TypeApi model = modelPath.type();
      switch (model.kind()) {
        case RECORD:
          GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?, ?> recordPath =
              (GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?, ?>) modelPath;

          GenFieldProjectionEntry<?, ?, ?, ?> fieldProjection = recordPath.pathFieldProjection();
          if (fieldProjection == null) break;
          len++;
          path = fieldProjection.fieldProjection().projection();
          break;
        case MAP:
          GenMapModelProjection<?, ?, ?, ?, ?, ?> mapPath = (GenMapModelProjection<?, ?, ?, ?, ?, ?>) modelPath;
          len++;
          path = mapPath.itemsProjection();
          break;
        default:
          break outer;
      }
    }

    return len;
  }

  public static @NotNull TypeApi mostSpecific(@NotNull TypeApi t1, @NotNull TypeApi t2, @NotNull TypeApi ifUnrelated) {
    if (t1.isAssignableFrom(t2)) return t2;
    if (t2.isAssignableFrom(t1)) return t1;
    return ifUnrelated;
  }

  public static @NotNull DatumTypeApi mostSpecific(
      @NotNull DatumTypeApi t1,
      @NotNull DatumTypeApi t2,
      @NotNull DatumTypeApi ifUnrelated) {

    if (t1.isAssignableFrom(t2)) return t2;
    if (t2.isAssignableFrom(t1)) return t1;
    return ifUnrelated;
  }

  public static <P extends GenProjection<P, ?, ?, ?>> @NotNull List<P> linearizeTails(
      @NotNull TypeApi t,
      @Nullable List<P> tails) {

    if (tails == null) return Collections.emptyList();

    return linearizeTails(
        p -> p.type(),
        P::polymorphicTails,
        t,
        tails
    );

  }

  @SuppressWarnings("unchecked")
  public static @NotNull <P extends GenProjection<P, ?, ?, ?>> P merge(
      List<? extends GenProjection<?,?,?,?>> p) {

    // merge heterogeneous list of projections assuming that actually they're all
    // of the same kind, i.e. either entity or model projections

    List<P> ps = ((List<P>) p);
    return ps.get(0).merge(ps);
  }

  @SuppressWarnings("unchecked")
  public static @NotNull <P extends GenProjection<P, ?, ?, ?>> P merge(
      @NotNull TypeApi targetType,
      @NotNull List<? extends GenProjection<?,?,?,?>> p) {

    // merge heterogeneous list of projections assuming that actually they're all
    // of the same kind, i.e. either entity or model projections

    List<P> ps = ((List<P>) p);
    return ps.get(0).merge(ps);
  }

  public static @NotNull <P extends GenProjection<P, ?, ?, ?>> List<P> linearizeTails(
      @NotNull TypeApi t,
      @NotNull Stream<P> tails,
      @NotNull LinkedList<P> linearizedTails) {

    return linearizeTails(
        p -> p.type(),
        P::polymorphicTails,
        t,
        tails,
        linearizedTails
    );

  }

  // generic tails linearization

  private static <P> @NotNull List<P> linearizeTails(
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

  private static <P> @NotNull List<P> linearizeTails(
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

  public static @NotNull ProjectionReferenceName normalizedTailNamespace(
      @NotNull ProjectionReferenceName baseProjectionName,
      @NotNull TypeApi tailType,
      boolean sameNamespace) {

    // keep in sync with ReqTypeProjectionGen
    // todo find some common place

    return baseProjectionName
        .append(new ProjectionReferenceName.StringRefNameSegment("_nt"))
        .append(new ProjectionReferenceName.TypeRefNameSegment(tailType, sameNamespace));
  }

  public static boolean sameNamespace(@NotNull TypeName n1, @NotNull TypeName n2) {
    if (n1 instanceof QualifiedTypeName) {
      QualifiedTypeName qn1 = (QualifiedTypeName) n1;

      if (n2 instanceof QualifiedTypeName) {
        QualifiedTypeName qn2 = (QualifiedTypeName) n2;

        return qn1.toFqn().sameNamespace(qn2.toFqn());
      } else return false;
    }

    if (n1 instanceof AnonListTypeName) {
      AnonListTypeName ltn1 = (AnonListTypeName) n1;

      if (n2 instanceof AnonListTypeName) {
        AnonListTypeName ltn2 = (AnonListTypeName) n2;

        return sameNamespace(ltn1.elementTypeName.typeName, ltn2.elementTypeName.typeName);
      } else return false;
    }

    if (n1 instanceof AnonMapTypeName) {
      AnonMapTypeName mtn1 = (AnonMapTypeName) n1;

      if (n2 instanceof AnonMapTypeName) {
        AnonMapTypeName mtn2 = (AnonMapTypeName) n2;

        return sameNamespace(mtn1.keyTypeName, mtn2.keyTypeName) &&
               sameNamespace(mtn1.valueTypeName.typeName, mtn2.valueTypeName.typeName);
      } else return false;
    }

    throw new RuntimeException("unreachable");
  }

  public static @Nullable ProjectionReferenceName buildReferenceName(
      @NotNull Collection<? extends GenProjectionReference<?>> elements,
      @NotNull TextLocation location) {

    ProjectionReferenceName res = null;

    for (final GenProjectionReference<?> element : elements) {
      final ProjectionReferenceName rn = element.referenceName();
      if (rn != null) {
        if (res == null) res = rn;
        else
//          throw new IllegalArgumentException(
//              String.format(
//                  "Merging multiple projection references is not yet supported. '%s' conflicts with '%s' (at %s)",
//                  rn.toString(), res.toString(), location
//              )
//          );
          return null;
      }
    }

    return res;
  }
}
