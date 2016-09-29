package io.epigraph.projections;

import io.epigraph.gdata.GDataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Annotations {
  @NotNull
  private final Map<String, Annotation> params;

  public Annotations(@NotNull Map<String, Annotation> params) {this.params = params;}

  public boolean hasParam(@NotNull String name) { return params.containsKey(name); }

  public boolean isEmpty() { return params.isEmpty(); }

  @Nullable
  public GDataValue get(@NotNull String key) {
    Annotation annotation = params.get(key);
    return annotation == null ? null : annotation.value();
  }

  @NotNull
  public Map<String, Annotation> params() { return params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Annotations opParams = (Annotations) o;
    return Objects.equals(params, opParams.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params);
  }
}
