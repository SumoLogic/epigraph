package io.epigraph.projections.op.input;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.data.Datum;
import io.epigraph.projections.generic.GenericModelProjection;
import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpInputModelProjection<M extends DatumType, D extends Datum>
    extends GenericModelProjection<M> implements PrettyPrintable {
  protected final boolean required;
  @Nullable
  protected final D defaultValue;
  @Nullable
  protected final OpCustomParams customParams;

  public OpInputModelProjection(@NotNull M model,
                                boolean required,
                                @Nullable D defaultValue,
                                @Nullable OpCustomParams customParams) {

    super(model);
    this.required = required;
    this.defaultValue = defaultValue;
    this.customParams = customParams;
  }

  public boolean required() { return required; }

  @Nullable
  public D defaultValue() { return defaultValue; }

  @Nullable
  public OpCustomParams customParams() { return customParams; }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    prettyPrintModel(l);
    prettyPrintParamsBlock(l);
    prettyPrintDefaultValueBlock(l);
  }

  protected <Exc extends Exception> void prettyPrintModel(DataLayouter<Exc> l) throws Exc {
    if (required) l.print("+");
    l.print(model.name().toString());
  }

  protected <Exc extends Exception> void prettyPrintDefaultValueBlock(DataLayouter<Exc> l) throws Exc {
    if (defaultValue != null) {
      l.beginCInd().print(" {");
      prettyPrintDefaultValue(l);
      l.end().brk().print("}");
    }
  }

  protected <E extends Exception> void prettyPrintDefaultValue(DataLayouter<E> l) throws E {
    assert defaultValue != null;
    l.print("default:").brk().print(defaultValue.toImmutable());
  }

  protected <Exc extends Exception> void prettyPrintParamsBlock(DataLayouter<Exc> l) throws Exc {
    if (customParams != null && !customParams.isEmpty()) l.print(customParams);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpInputModelProjection<?, ?> that = (OpInputModelProjection<?, ?>) o;
    return required == that.required &&
           Objects.equals(model, that.model) &&
           Objects.equals(defaultValue, that.defaultValue) &&
           Objects.equals(customParams, that.customParams);
  }

  @Override
  public int hashCode() { return Objects.hash(model, required, defaultValue, customParams); }
}
