package io.epigraph.projections.op;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpParams implements PrettyPrintable {
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

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    for (OpParam param : params.values()) l.brk().print(param);
  }

  @Override
  public String toString() { return DataPrettyPrinter.prettyPrint(this); }
}
