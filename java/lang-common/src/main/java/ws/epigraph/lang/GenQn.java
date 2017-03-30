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

package ws.epigraph.lang;

import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Generic qualified name is a collection of segments
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@Immutable
public abstract class GenQn<T, S extends GenQn<T, S>> {
  private final Class<T> elementClass;
  public final @NotNull T[] segments;

  @SafeVarargs
  protected GenQn(@NotNull Class<T> elementClass, @NotNull T... segments) {
    this.elementClass = elementClass;
    this.segments = newArray(segments.length);
    System.arraycopy(segments, 0, this.segments, 0, segments.length);

    // auto-canonicalize
    for (int i = 0; i < segments.length; i++)
      this.segments[i] = sanitize(this.segments[i]);
  }

  protected GenQn(@NotNull Class<T> elementClass, @NotNull T[] segments, boolean copy) {
    this.elementClass = elementClass;
    if (copy) {
      this.segments = newArray(segments.length);
      System.arraycopy(segments, 0, this.segments, 0, segments.length);
    } else this.segments = segments;
  }

  private @NotNull T[] newArray(int size) {
    //noinspection unchecked
    return (T[]) Array.newInstance(elementClass, size);
  }

  @SuppressWarnings("unchecked")
  private S self() { return (S) this;}

  protected abstract S newInstance(@NotNull T[] segments, boolean copy);

  protected abstract S emptyInstance();

  protected @NotNull T sanitize(@NotNull T segment) { return segment; }

  public int size() { return segments.length; }

  public boolean isEmpty() { return size() == 0; }

  public @Nullable T first() {
    if (isEmpty()) return null;
    return segments[0];
  }

  public @Nullable T last() {
    if (isEmpty()) return null;
    return segments[size() - 1];
  }

  public @NotNull S takeHeadSegments(int n) {
    if (size() < n) throw new IllegalArgumentException("Can't take " + n + " segments from '" + toString() + "'");
    return removeTailSegments(size() - n);
  }

  public @NotNull S removeLastSegment() {
    return removeTailSegments(1);
  }

  public @NotNull S removeFirstSegment() {
    return removeHeadSegments(1);
  }

  public @NotNull S removeHeadSegments(int n) {
    if (n < 0 || size() < n)
      throw new IllegalArgumentException("Can't remove " + n + " segments from '" + toString() + "'");
    if (size() == n) return emptyInstance();
    if (n == 0) return self();

    T[] f = newArray(size() - n);
    System.arraycopy(segments, n, f, 0, size() - n);
    return newInstance(f, false);
  }

  public @NotNull S removeTailSegments(int n) {
    if (n < 0 || size() < n)
      throw new IllegalArgumentException("Can't remove " + n + " segments from '" + toString() + "'");
    if (size() == n) return emptyInstance();
    if (n == 0) return self();

    T[] f = newArray(size() - n);
    System.arraycopy(segments, 0, f, 0, size() - n);
    return newInstance(f, false);
  }

  public @NotNull S append(@NotNull S suffix) {
    if (isEmpty()) return suffix;
    if (suffix.isEmpty()) return self();

    T[] f = newArray(size() + suffix.size());
    System.arraycopy(segments, 0, f, 0, size());
    System.arraycopy(suffix.segments, 0, f, size(), suffix.size());
    return newInstance(f, false);
  }

  public @NotNull S append(@NotNull T segment) {
    T[] f = newArray(size() + 1);
    System.arraycopy(segments, 0, f, 0, size());
    f[size()] = sanitize(segment);
    return newInstance(f, false);
  }

  public boolean startsWith(@NotNull S prefix) {
    if (prefix.isEmpty()) return true;
    if (size() < prefix.size()) return false;

    for (int i = 0; i < prefix.size(); i++) {
      if (!segments[i].equals(prefix.segments[i])) return false;
    }

    return true;
  }

  public boolean endsWith(@NotNull S suffix) {
    if (suffix.isEmpty()) return true;
    int diff = size() - suffix.size();
    if (diff < 0) return false;

    for (int i = 0; i < suffix.size(); i++) {
      if (!segments[i + diff].equals(suffix.segments[i])) return false;
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  public @NotNull S map(@NotNull Function<T, T> f) {
    S res = newInstance(newArray(size()), false);
    for (int i = 0; i < size(); i++) {
      res.segments[i] = f.apply(segments[i]);
    }
    return res;
  }

  @Override
  public String toString() {
    StringBuilder r = new StringBuilder();
    for (T segment : segments) {
      if (r.length() > 0) r.append('.');
      r.append(segment);
    }

    return r.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GenQn<?, ?> fqn = (GenQn<?, ?>) o;

    return Arrays.equals(segments, fqn.segments);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(segments);
  }
}
