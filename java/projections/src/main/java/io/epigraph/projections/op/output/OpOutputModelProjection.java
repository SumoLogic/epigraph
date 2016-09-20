package io.epigraph.projections.op.output;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.projections.generic.GenericModelProjection;
import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.projections.op.OpParams;
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
  @Nullable
  protected final OpCustomParams customParams;

  public OpOutputModelProjection(@NotNull M model,
                                 boolean includeInDefault,
                                 @Nullable OpParams params,
                                 @Nullable OpCustomParams customParams) {
    super(model);
    this.includeInDefault = includeInDefault;
    this.params = params;
    this.customParams = customParams;
  }

  public boolean includeInDefault() { return includeInDefault; }

  @Nullable
  public OpParams params() { return params; }

  @Nullable
  public OpCustomParams customParams() { return customParams; }

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
    if (params != null && !params.isEmpty()) l.brk().print(params);
    if (customParams != null && !customParams.isEmpty()) l.brk().print(customParams);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpOutputModelProjection<?> that = (OpOutputModelProjection<?>) o;
    return includeInDefault == that.includeInDefault &&
           Objects.equals(model, that.model) &&
           Objects.equals(params, that.params) &&
           Objects.equals(customParams, that.customParams);
  }

  @Override
  public int hashCode() { return Objects.hash(model, includeInDefault, params, customParams); }
}
