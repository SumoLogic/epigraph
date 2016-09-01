package io.epigraph.projections.op;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.types.DatumType;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpOutputModelProjection<M extends DatumType, P extends OpOutputModelProjection<M, P>> implements PrettyPrintable {
  @NotNull
  protected final M model;
  protected final boolean required;
  @Nullable
  protected final Set<OpParam> params;
  @Nullable
  protected final LinkedHashSet<P> polymorphicTail;

  public OpOutputModelProjection(@NotNull M model,
                                 boolean required,
                                 @Nullable Set<OpParam> params,
                                 @Nullable LinkedHashSet<P> polymorphicTail) {
    this.model = model;
    this.required = required;
    this.params = params;
    this.polymorphicTail = polymorphicTail;
  }

  public M model() {
    return model;
  }

  public boolean required() {
    return required;
  }

  public @Nullable Set<OpParam> params() {
    return params;
  }

  public @Nullable LinkedHashSet<P> polymorphicTail() {
    return polymorphicTail;
  }

  @NotNull
  public P projectionForModel(@NotNull M model) {
    // todo merge self + appropriate tails
    throw new UnsupportedOperationException();
  }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    prettyPrintModel(l);
    if (params != null && !params.isEmpty()) {
      l.beginCInd().print(" {");
      prettyPrintParams(l, params);
      l.end().brk().print("}");
    }
    if (polymorphicTail != null && !polymorphicTail.isEmpty()) prettyPrintTail(l, polymorphicTail);
  }

  protected <Exc extends Exception> void prettyPrintModel(DataLayouter<Exc> l) throws Exc {
    if (required) l.print("+");
    l.print(model.name().toString());
  }

  protected <Exc extends Exception> void prettyPrintParams(DataLayouter<Exc> l, @NotNull Collection<OpParam> params) throws Exc {
    for (OpParam param : params) {
      l.brk().print(param);
    }
  }

  protected <Exc extends Exception> void prettyPrintTail(DataLayouter<Exc> l, @NotNull Collection<P> polymorphicTail) throws Exc {
    l.brk().beginCInd().print("~(");
    for (P p : polymorphicTail) {
      l.brk().print(p);
    }
    l.end().brk().print(")");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpOutputModelProjection<?, ?> that = (OpOutputModelProjection<?, ?>) o;
    return required == that.required &&
        Objects.equals(model, that.model) &&
        Objects.equals(params, that.params) &&
        Objects.equals(polymorphicTail, that.polymorphicTail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(model, required, params, polymorphicTail);
  }

  @Override
  public String toString() {
    return DataPrettyPrinter.prettyPrint(this);
  }
}
