package io.epigraph.projections;

import io.epigraph.gdata.GDataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Annotations {
  @NotNull
  private final Map<String, Annotation> entries;

  public Annotations(@NotNull Map<String, Annotation> entries) {this.entries = entries;}

  public Annotations(@NotNull Collection<Annotation> annotations) {
    this(annotations.stream().collect(Collectors.toMap(Annotation::name, Function.identity())));
  }

  public boolean hasParam(@NotNull String name) { return entries.containsKey(name); }

  public boolean isEmpty() { return entries.isEmpty(); }

  @Nullable
  public GDataValue get(@NotNull String key) {
    Annotation annotation = entries.get(key);
    return annotation == null ? null : annotation.value();
  }

  @NotNull
  public Map<String, Annotation> params() { return entries; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Annotations opParams = (Annotations) o;
    return Objects.equals(entries, opParams.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entries);
  }
}
