package io.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpParams {
  @NotNull
  private final Map<String, OpParam> params;

  public OpParams(@NotNull Map<String, OpParam> params) {this.params = params;}

  public OpParams(OpParam... params) {
    this.params = new HashMap<>();
    for (OpParam param : params)
      this.params.put(param.name(), param);
  }

  public OpParams(@NotNull Collection<OpParam> params) {
    this.params = new HashMap<>();
    for (OpParam param : params)
      this.params.put(param.name(), param);
  }

  public boolean hasParam(@NotNull String name) {
    return params.containsKey(name);
  }

  public boolean isEmpty() {
    return params.isEmpty();
  }

  @Nullable
  public OpParam get(@NotNull String key) {
    return params.get(key);
  }

  @NotNull
  public Map<String, OpParam> params() { return params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpParams opParams = (OpParams) o;
    return Objects.equals(params, opParams.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params);
  }
}
