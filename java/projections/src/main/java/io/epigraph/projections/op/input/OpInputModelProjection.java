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
