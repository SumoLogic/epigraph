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

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fqn is a collection of string segments
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@Immutable
public class Qn implements Comparable<Qn> {
  // todo this often acts as Fqn prefix/suffix. Rename to Qn ?

  public static final Qn EMPTY = new Qn();

  public final @NotNull String[] segments;

  public Qn(@NotNull String... segments) {
    this.segments = new String[segments.length];
    System.arraycopy(segments, 0, this.segments, 0, segments.length);

    // auto-canonicalize
    for (int i = 0; i < segments.length; i++)
      this.segments[i] = NamingConventions.unquote(this.segments[i]);
  }

  public Qn(@NotNull Collection<String> segments) {
    this(segments.toArray(new String[segments.size()]));
  }

  private Qn(@NotNull String[] segments, boolean copy) {
    if (copy) {
      this.segments = new String[segments.length];
      System.arraycopy(segments, 0, this.segments, 0, segments.length);
    } else this.segments = segments;
  }

  public static @NotNull Qn fromDotSeparated(@NotNull String fqn) {
    // todo will break if dot is inside back-ticks..
    return new Qn(fqn.split("\\."));
  }

  public static @Nullable Qn fromNullableDotSeparated(@Nullable String fqn) {
    return fqn == null ? null : fromDotSeparated(fqn);
  }

  public int size() {
    return segments.length;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public @Nullable String first() {
    if (isEmpty()) return null;
    return segments[0];
  }

  public @Nullable String last() {
    if (isEmpty()) return null;
    return segments[size() - 1];
  }

  public @NotNull Qn takeHeadSegments(int n) {
    if (size() < n) throw new IllegalArgumentException("Can't take " + n + " segments from '" + toString() + "'");
    return removeTailSegments(size() - n);
  }

  public @NotNull Qn removeLastSegment() {
    return removeTailSegments(1);
  }

  public @NotNull Qn removeFirstSegment() {
    return removeHeadSegments(1);
  }

  public @NotNull Qn removeHeadSegments(int n) {
    if (n < 0 || size() < n)
      throw new IllegalArgumentException("Can't remove " + n + " segments from '" + toString() + "'");
    if (size() == n) return EMPTY;
    if (n == 0) return this;

    String[] f = new String[size() - n];
    System.arraycopy(segments, n, f, 0, size() - n);
    return new Qn(f);
  }

  public @NotNull Qn removeTailSegments(int n) {
    if (n < 0 || size() < n)
      throw new IllegalArgumentException("Can't remove " + n + " segments from '" + toString() + "'");
    if (size() == n) return EMPTY;
    if (n == 0) return this;

    String[] f = new String[size() - n];
    System.arraycopy(segments, 0, f, 0, size() - n);
    return new Qn(f);
  }

  public @NotNull Qn append(@NotNull Qn suffix) {
    if (isEmpty()) return suffix;
    if (suffix.isEmpty()) return this;

    String[] f = new String[size() + suffix.size()];
    System.arraycopy(segments, 0, f, 0, size());
    System.arraycopy(suffix.segments, 0, f, size(), suffix.size());
    return new Qn(f);
  }

  public @NotNull Qn append(@NotNull String segment) {
    String[] f = new String[size() + 1];
    System.arraycopy(segments, 0, f, 0, size());
    f[size()] = NamingConventions.unquote(segment);
    return new Qn(f);
  }

  public boolean startsWith(@NotNull Qn prefix) {
    if (prefix.isEmpty()) return true;
    if (size() < prefix.size()) return false;

    for (int i = 0; i < prefix.size(); i++) {
      if (!segments[i].equals(prefix.segments[i])) return false;
    }

    return true;
  }

  public boolean endsWith(@NotNull Qn suffix) {
    if (suffix.isEmpty()) return true;
    int diff = size() - suffix.size();
    if (diff < 0) return false;

    for (int i = 0; i < suffix.size(); i++) {
      if (!segments[i + diff].equals(suffix.segments[i])) return false;
    }

    return true;
  }

  public @NotNull Qn map(@NotNull Function<String, String> f) {
    Qn res = new Qn(new String[size()], false);
    for (int i = 0; i < size(); i++) {
      res.segments[i] = f.apply(segments[i]);
    }
    return res;
  }

  public @NotNull Qn toLower() {
    return map(String::toLowerCase);
  }

  @Override
  public String toString() {
    StringBuilder r = new StringBuilder();
    for (String segment : segments) {
      if (r.length() > 0) r.append('.');
      r.append(segment);
    }

    return r.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Qn fqn = (Qn) o;

    return Arrays.equals(segments, fqn.segments);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(segments);
  }

  public static @Nullable String toNullableString(@Nullable Qn fqn) {
    return fqn == null ? null : fqn.toString();
  }

  @Override
  public int compareTo(@NotNull Qn fqn) {
    return toString().compareTo(fqn.toString());
  }

  /**
   * Find all FQNs starting with {@code prefix} and remove prefix from them
   */
  public static Collection<Qn> getMatchingWithPrefixRemoved(@NotNull Collection<Qn> qns, @NotNull Qn prefix) {
    if (prefix.isEmpty()) return qns;
    return qns.stream()
        .filter(fqn -> fqn.startsWith(prefix))
        .map(fqn -> fqn.removeHeadSegments(prefix.size()))
//        .filter(fqn -> !fqn.isEmpty())
        .collect(Collectors.toSet());
  }
}
