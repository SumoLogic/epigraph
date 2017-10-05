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

package ws.epigraph.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class Tuple2<T1, T2> {
  public final T1 _1;
  public final T2 _2;

  public Tuple2(final T1 _1, final T2 _2) {
    this._1 = _1;
    this._2 = _2;
  }

  public static @NotNull <T1, T2> Tuple2<T1, T2> of(T1 _1, T2 _2) { return new Tuple2<>(_1, _2); }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
    return Objects.equals(_1, tuple2._1) &&
           Objects.equals(_2, tuple2._2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_1, _2);
  }

  @Override
  public String toString() {
    return String.format("(%s,%s)", _1, _2);
  }
}
