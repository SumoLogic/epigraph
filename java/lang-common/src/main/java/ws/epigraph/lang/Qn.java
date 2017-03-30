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

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Qualified name is a collection of string segments
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@Immutable
public class Qn extends GenQn<String, Qn> implements Comparable<Qn> {
  public static final Qn EMPTY = new Qn();

  public Qn(@NotNull String... segments) {
    super(String.class, segments);
  }

  public Qn(@NotNull Collection<String> segments) {
    this(segments.toArray(new String[segments.size()]));
  }

  private Qn(@NotNull String[] segments, boolean copy) {
    super(String.class, segments, copy);
  }

  @Override
  protected Qn newInstance(final @NotNull String[] segments, final boolean copy) {
    return new Qn(segments, copy);
  }

  @Override
  protected Qn emptyInstance() { return EMPTY; }

  public static @NotNull Qn fromDotSeparated(@NotNull String fqn) {
    // todo will break if dot is inside back-ticks..
    return new Qn(fqn.split("\\."));
  }

  public static @Nullable Qn fromNullableDotSeparated(@Nullable String fqn) {
    return fqn == null ? null : fromDotSeparated(fqn);
  }

  @Override
  protected @NotNull String sanitize(final @NotNull String segment) {
    return NamingConventions.unquote(segment);
  }

  public @NotNull Qn toLower() { return map(String::toLowerCase); }

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
