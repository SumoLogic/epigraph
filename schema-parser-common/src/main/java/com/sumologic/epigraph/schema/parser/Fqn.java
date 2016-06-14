package com.sumologic.epigraph.schema.parser;

import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Fqn is a collection of string segments
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
@Immutable
public class Fqn {
  public static final Fqn EMPTY = new Fqn();

  @NotNull
  public final String[] segments;

  public Fqn(@NotNull String... segments) {
    this.segments = segments;
  }

  public Fqn(@NotNull Collection<String> segments) {
    this(segments.toArray(new String[segments.size()]));
  }

  @NotNull
  public static Fqn fromDotSeparated(@NotNull String fqn) {
    return new Fqn(fqn.split("\\."));
  }

  @Nullable
  public static Fqn fromNullableDotSeparated(@Nullable String fqn) {
    return fqn == null ? null : fromDotSeparated(fqn);
  }

  public int size() {
    return segments.length;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  @Nullable
  public String first() {
    if (isEmpty()) return null;
    return segments[0];
  }

  @Nullable
  public String last() {
    if (isEmpty()) return null;
    return segments[size() - 1];
  }

  @NotNull
  public Fqn removeLastSegment() {
    return removeTailSegments(1);
  }

  @NotNull
  public Fqn removeFirstSegment() {
    return removeHeadSegments(1);
  }

  @NotNull
  public Fqn removeHeadSegments(int n) {
    if (size() < n) throw new IllegalArgumentException("Can't remove " + n + " segments from '" + toString() + "'");
    if (size() == n) return EMPTY;

    String[] f = new String[size() - n];
    System.arraycopy(segments, n, f, 0, size() - n);
    return new Fqn(f);
  }

  @NotNull
  private Fqn removeTailSegments(int n) {
    if (size() < n) throw new IllegalArgumentException("Can't remove " + n + " segments from '" + toString() + "'");
    if (size() == n) return EMPTY;

    String[] f = new String[size() - n];
    System.arraycopy(segments, 0, f, 0, size() - n);
    return new Fqn(f);
  }

  @NotNull
  public Fqn append(@NotNull Fqn suffix) {
    if (isEmpty()) return suffix;
    if (suffix.isEmpty()) return this;

    String[] f = new String[size() + suffix.size()];
    System.arraycopy(segments, 0, f, 0, size());
    System.arraycopy(suffix.segments, 0, f, size(), suffix.size());
    return new Fqn(f);
  }

  @NotNull
  public Fqn append(@NotNull String segment) {
    String[] f = new String[size() + 1];
    System.arraycopy(segments, 0, f, 0, size());
    f[size()] = segment;
    return new Fqn(f);
  }

  public boolean startsWith(@NotNull Fqn prefix) {
    if (prefix.isEmpty()) return true;
    if (size() < prefix.size()) return false;

    for (int i = 0; i < prefix.size(); i++) {
      if (!segments[i].equals(prefix.segments[i])) return false;
    }

    return true;
  }

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

    Fqn fqn = (Fqn) o;

    return Arrays.equals(segments, fqn.segments);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(segments);
  }

  @Nullable
  public static String toNullableString(@Nullable Fqn fqn) {
    return fqn == null ? null : fqn.toString();
  }

  /**
   * Find all FQNs starting with {@code prefix} and remove prefix from them
   */
  public static Collection<Fqn> getMatchingWithPrefixRemoved(@NotNull Collection<Fqn> fqns, @NotNull Fqn prefix) {
    if (prefix.isEmpty()) return fqns;
    return fqns.stream()
        .filter(fqn -> fqn.startsWith(prefix))
        .map(fqn -> fqn.removeHeadSegments(prefix.size()))
//        .filter(fqn -> !fqn.isEmpty())
        .collect(Collectors.toSet());
  }
}