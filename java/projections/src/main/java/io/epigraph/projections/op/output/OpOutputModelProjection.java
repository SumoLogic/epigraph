package io.epigraph.projections.op.output;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.projections.generic.GenericModelProjection;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpOutputModelProjection<M extends DatumType>
    extends GenericModelProjection<M> implements PrettyPrintable {
  protected final boolean includeInDefault;
  @Nullable
  protected final OpParams params;

  public OpOutputModelProjection(@NotNull M model,
                                 boolean includeInDefault,
                                 @Nullable OpParams params) {
    super(model);
    this.includeInDefault = includeInDefault;
    this.params = params;
  }

  public boolean includeInDefault() { return includeInDefault; }

  public @Nullable OpParams params() { return params; }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    prettyPrintModel(l);
    prettyPrintParamsBlock(l);
  }

  protected <Exc extends Exception> void prettyPrintModel(DataLayouter<Exc> l) throws Exc {
    if (includeInDefault) l.print("+");
    l.print(model.name().toString());
  }

  protected <Exc extends Exception> void prettyPrintParamsBlock(DataLayouter<Exc> l) throws Exc {
    if (params != null && !params.isEmpty()) l.print(params);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpOutputModelProjection<?> that = (OpOutputModelProjection<?>) o;
    return includeInDefault == that.includeInDefault &&
           Objects.equals(model, that.model) &&
           Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() { return Objects.hash(model, includeInDefault, params); }
}
