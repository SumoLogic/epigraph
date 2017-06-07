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

package ws.epigraph.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Raw data comparator
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DataComparator {

  private final IdentityHashMap<Data, Set<Data>> visited = new IdentityHashMap<>();

  private DataComparator() {}

  public static boolean equals(@Nullable Data d1, @Nullable Data d2) { return new DataComparator().equal(d1, d2); }

  public static boolean equals(@Nullable Val v1, @Nullable Val v2) { return new DataComparator().equal(v1, v2); }

  public static boolean equals(@Nullable Datum d1, @Nullable Datum d2) { return new DataComparator().equal(d1, d2); }

  private boolean equal(@Nullable Data d1, @Nullable Data d2) {
    if (d1 == d2) return true;
    if (d1 == null || d2 == null) return false;

    Set<Data> v = visited.get(d1);

    if (v == null) {
      v = Collections.newSetFromMap(new IdentityHashMap<Data, Boolean>());
      visited.put(d1, v);
    } else {
      if (v.contains(d2))
        return true;
    }

    v.add(d2);

    try {
      if (!d1.type().equals(d2.type())) return false;

      final Map<@NotNull String, @NotNull ? extends Val> tv1 = d1._raw().tagValues();
      final Map<@NotNull String, @NotNull ? extends Val> tv2 = d2._raw().tagValues();

      if (tv1.size() != tv2.size()) return false;
      if (!tv1.keySet().equals(tv2.keySet())) return false;

      for (final Map.Entry<String, ? extends Val> entry : tv1.entrySet()) {
        if (!equal(entry.getValue(), tv2.get(entry.getKey()))) return false;
      }

      return true;
    } finally {
      v.remove(d2);

      if (v.isEmpty())
        visited.remove(d1);
    }
  }

  private boolean equal(@Nullable Val v1, @Nullable Val v2) {
    if (v1 == v2) return true;
    if (v1 == null || v2 == null) return false;

    //noinspection SimplifiableIfStatement
    if (!Objects.equals(v1.getError(), v2.getError())) return false;

    return equal(v1.getDatum(), v2.getDatum());
  }

  private boolean equal(@Nullable Datum d1, @Nullable Datum d2) {
    if (d1 == d2) return true;
    if (d1 == null || d2 == null) return false;

    if (!d1.type().equals(d2.type())) return false;

    switch (d1.type().kind()) {
      case ENTITY:
        throw new IllegalArgumentException("Unsupported model kind: " + d1.type().name());
      case RECORD:
        return equal((RecordDatum) d1, (RecordDatum) d2);
      case MAP:
        return equal((MapDatum) d1, (MapDatum) d2);
      case LIST:
        return equal((ListDatum) d1, (ListDatum) d2);
      case PRIMITIVE:
        return equal((PrimitiveDatum<?>) d1, (PrimitiveDatum<?>) d2);
      case ENUM:
        throw new IllegalArgumentException("Unsupported model kind: " + d1.type().name());
      default:
        throw new IllegalArgumentException("Unsupported model kind: " + d1.type().name());
    }
  }

  private boolean equal(@NotNull RecordDatum d1, @NotNull RecordDatum d2) {
    final Map<@NotNull String, @NotNull ? extends Data> fd1 = d1._raw().fieldsData();
    final Map<@NotNull String, @NotNull ? extends Data> fd2 = d2._raw().fieldsData();

    if (fd1.size() != fd2.size()) return false;
    if (!fd1.keySet().equals(fd2.keySet())) return false;

    for (final Map.Entry<String, ? extends Data> entry : fd1.entrySet()) {
      if (!equal(entry.getValue(), fd2.get(entry.getKey()))) return false;
    }

    return true;
  }

  private boolean equal(@NotNull MapDatum d1, @NotNull MapDatum d2) {
    if (d1.size() != d2.size()) return false;

    final Map<Datum.@NotNull Imm, @NotNull ? extends Data> e1 = d1._raw().elements();
    final Map<Datum.@NotNull Imm, @NotNull ? extends Data> e2 = d2._raw().elements();

    if (!e1.keySet().equals(e2.keySet())) return false; // keys are immutable and cannot be recursive

    for (final Map.Entry<Datum.Imm, ? extends Data> entry : e1.entrySet()) {
      if (!equal(entry.getValue(), e2.get(entry.getKey()))) return false;
    }

    return true;
  }

  private boolean equal(@NotNull ListDatum d1, @NotNull ListDatum d2) {
    if (d1.size() != d2.size()) return false;

    final Iterator<@NotNull ? extends Data> e1 = d1._raw().elements().iterator();
    final Iterator<@NotNull ? extends Data> e2 = d2._raw().elements().iterator();

    while (e1.hasNext()) if (!equal(e1.next(), e2.next())) return false;

    return true;
  }

  private static boolean equal(@NotNull PrimitiveDatum<?> d1, @NotNull PrimitiveDatum<?> d2) {
    return (d1.getVal().equals(d2.getVal()));
  }

}
