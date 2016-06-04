package com.sumologic.epigraph.ideaplugin.schema.brains;

import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 * // TODO move to ..
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

  public int size() {
    return segments.length;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  @Nullable
  public String getFirst() { // TODO rename to first
    if (isEmpty()) return null;
    return segments[0];
  }

  @Nullable
  public String getLast() { // TODO rename to last
    if (isEmpty()) return null;
    return segments[size() - 1];
  }

  @NotNull
  public Fqn removeLastSegment() {
    if (isEmpty()) throw new IllegalArgumentException("Can't remove last segment from an empty Fqn");
    if (size() == 1) return EMPTY;
    String[] f = new String[size() - 1];
    System.arraycopy(segments, 0, f, 0, size() - 1);
    return new Fqn(f);
  }

  @NotNull
  public Fqn removeFirstSegment() {
    if (isEmpty()) throw new IllegalArgumentException("Can't remove first segment from an empty Fqn");
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

  @Nullable
  public Fqn getPrefix() { // todo rename to prefix, add javadoc
    if (isEmpty()) return null;
    if (size() == 1) return EMPTY;

    String[] f = new String[size() - 1];
    System.arraycopy(segments, 0, f, 0, size() - 1);
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

  /**
   * Find all FQNs with last segment being `lastSegment` (or just all if it's null) and remove last segment from them
   */
  public static Set<Fqn> getMatchingWithSuffixRemoved(@NotNull Collection<Fqn> fqns, @Nullable String lastSegment) {
    return fqns.stream()
        .filter(fqn -> lastSegment == null || lastSegment.equals(fqn.getLast()))
        .map(Fqn::getPrefix)
        .filter(fqn -> fqn != null)
        .collect(Collectors.toSet());
  }

  /**
   * Find all FQNs starting with `prefix` (which may have dots) and remove prefix from them
   */
  public static Set<Fqn> getMatchingWithPrefixRemoved(@NotNull Collection<Fqn> fqns, @NotNull String prefix) {
    int segmentsToRemove = prefix.isEmpty() ? 0 : prefix.length() - prefix.replace(".", "").length() + 1;

    return fqns.stream()
        .map(fqn -> fqn.removeHeadSegments(segmentsToRemove))
        .filter(fqn -> !fqn.isEmpty())
        .collect(Collectors.toSet());
  }
}
