package io.epigraph.projections.op;

import io.epigraph.gdata.GDataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpCustomParams {
  @NotNull
  private final Map<String, OpCustomParam> params;

  public OpCustomParams(@NotNull Map<String, OpCustomParam> params) {this.params = params;}

  public boolean hasParam(@NotNull String name) { return params.containsKey(name); }

  public boolean isEmpty() { return params.isEmpty(); }

  @Nullable
  public GDataValue get(@NotNull String key) {
    OpCustomParam customParam = params.get(key);
    return customParam == null ? null : customParam.value();
  }

  @NotNull
  public Map<String, OpCustomParam> params() { return params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpCustomParams opParams = (OpCustomParams) o;
    return Objects.equals(params, opParams.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params);
  }
}
